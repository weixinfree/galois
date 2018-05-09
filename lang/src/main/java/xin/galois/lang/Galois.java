package xin.galois.lang;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xin.galois.lang.atom.AbsRegexAtom;
import xin.galois.lang.atom.Atom;
import xin.galois.lang.builtin.BasicBoolOp;
import xin.galois.lang.internal.StringIterable;

import static java.lang.Character.isWhitespace;

/**
 * Galois Lang Interpreter
 * Created by wangwei on 2018/5/2.
 */
public class Galois {

    // TODO-wei: 2018/5/4 lazy eval / eager eval
    // TODO-wei: 2018/5/4 closure
    // TODO-wei: 2018/5/4 dynamic scope / lexical scope
    // TODO-wei: 2018/5/4 block scope / function scope
    // TODO-wei: 2018/5/6 反射调用原始类型问题
    // TODO-wei: 2018/5/6 数学运算默认值为double问题
    // TODO-wei: 2018/5/6 不同数字类型无法比较问题
    // TODO-wei: 2018/5/6 匿名函数
    // TODO-wei: 2018/5/6 import
    // TODO-wei: 2018/5/6 解包
    // TODO-wei: 2018/5/6 高阶函数
    // TODO-wei: 2018/5/6 注释 (;; this is comment)
    // TODO-wei: 2018/5/6 ()
    // TODO-wei: 2018/5/6 针对多行代码的错误提示
    // TODO-wei: 2018/5/6 文档
    // TODO-wei: 2018/5/7 选择开源证书
    // TODO-wei: 2018/5/7 字符串不支持转义 \'
    // TODO-wei: 2018/5/8 ast 裁剪

    ///////////////////////////////////////////////////////////////////////////
    // static
    ///////////////////////////////////////////////////////////////////////////

    public static final Object None = new Object() {
        @Override
        public String toString() {
            return "None";
        }
    };

    public static final Set<String> KEYWORDS = Collections.unmodifiableSet(new HashSet<String>() {{
        add("if");
        add("let");
        add("do");
        add("iter");
        add("fn");
        add("record");
        add("$index");
        add("$it");
        add("$*");
    }});

    public static Object evalS(String se) {
        return new Galois().eval(se);
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////

    Env env;

    public Galois() {
        this.env = new Env();
        env.pushNewEnv();

        this.atoms = new LinkedHashSet<>();
        BuiltIns.installAtoms(this);

        this.operators = new LinkedHashMap<>();
        BuiltIns.installFunctor(this);

        atoms.add(new OpAtom());
        atoms.add(new VarAtom());
    }

    public Galois(Galois galois) {
        this.env = galois.env.snapshot();

        this.atoms = new LinkedHashSet<>();
        BuiltIns.installAtoms(this);

        this.operators = galois.operators;

        atoms.add(new OpAtom());
        atoms.add(new VarAtom());
    }

    private final Map<String, Functor> operators;

    public void registerFunctor(String opName, Functor op) {
        operators.put(opName, op);
    }

    private final Set<Atom> atoms;

    public void registerAtom(Atom Atom) {
        atoms.add(Atom);
    }


    ///////////////////////////////////////////////////////////////////////////
    // eval
    ///////////////////////////////////////////////////////////////////////////

    private final Object lock = new Object();

    private String se;
    private int index;
    private List<Integer> lineEndMark;

    public Object eval(String se) {

        checkParenthesisMatch(se);

        // 只能单线程执行
        synchronized (lock) {
            this.se = se;
            this.index = 0;
            this.lineEndMark = new ArrayList<>();

            final Object result = evalInternal();
            assertTrue(index == se.length(), "syntax error, eval finished, but not cost all code");
            return result;
        }
    }

    private Object temp_eval(String se, boolean newEnv) {
        final String globalSe = this.se;
        final int globalIndex = this.index;
        final List<Integer> lineMark = this.lineEndMark;

        if (newEnv) {
            env.pushNewEnv();
        }

        try {
            this.se = se;
            this.index = 0;
            this.lineEndMark = new ArrayList<>();
            final Object result = evalInternal();
            assertTrue(index == se.length(), "syntax error, eval finished, but not cost all code");
            return result;
        } finally {

            if (newEnv) {
                env.popEnv();
            }
            this.se = globalSe;
            this.index = globalIndex;
            this.lineEndMark = lineMark;
        }
    }


    private Object evalInternal() {

        // s expression
        if (peek_char() == '(') {
            consume('(');
            final String operator = consume_operator();

            consume_whitespace();

            switch (operator) {
                case "if":
                    return eval_if();
                case "iter":
                    return eval_iter();
                case "let":
                    return eval_let();
                case "fn":
                    return eval_fn();
                case "do":
                    return eval_do();
                case "record":
                    return eval_record();
                default:
                    if (recordDefines.contains(operator)) {
                        return make_record(operator);
                    } else {
                        return eval_op(operator);
                    }
            }

        } else {
            // atom

            consume_whitespace();
            final Object atom = eval_atom();
            consume_whitespace();

            return atom;
        }
    }

    private Object eval_op(String operator) {

        List<Object> params = new ArrayList<>();
        while (peek_char() != ')') {
            params.add(evalInternal());
            consume_whitespace();
        }

        consume(')');

        return eval_op(operator, params);
    }

    private Object eval_if() {
        final Object _test = consume_sexpr_or_atom();
        consume_whitespace();
        final Object _ifTrue = consume_sexpr_or_atom();
        consume_whitespace();
        final Object _ifFalse = consume_sexpr_or_atom();
        consume_whitespace();
        consume(')');

        final Object test;
        if (isValidSExpr(_test)) {
            test = temp_eval(((String) _test), false);
        } else {
            test = _test;
        }

        if (BasicBoolOp.bool(test)) {

            if (isValidSExpr(_ifTrue)) {
                return temp_eval(((String) _ifTrue), true);
            }

            return _ifTrue;

        } else {

            if (isValidSExpr(_ifFalse)) {
                return temp_eval(((String) _ifFalse), true);
            }

            return _ifFalse;
        }
    }

    private Object eval_iter() {
        final Object _iterable = consume_sexpr_or_atom();
        consume_whitespace();
        final String _action = consume_sexpr();
        consume_whitespace();
        consume(')');

        if (_action.isEmpty()) {
            return fatal("wrong iter syntax: <(iter iterable (action))>");
        }

        final Object iter = isValidSExpr(_iterable) ? temp_eval(((String) _iterable), false) : _iterable;

        env.pushNewEnv();

        Object result = None;
        try {
            int index = 0;
            for (Object item : toIterable(iter)) {

                env.set("$index", index);
                env.set("$it", item);
                result = temp_eval(_action, false);

                index++;
            }
        } finally {
            env.popEnv();
        }

        return result;
    }

    private final Set<String> funcDefines = new HashSet<>();

    private Object eval_fn() {
        final String _fn_name = consume_sexpr_or_word();
        consume_whitespace();
        final String _params = consume_sexpr();
        consume_whitespace();
        final String _body = consume_sexpr();
        consume_whitespace();
        consume(')');

        if (_fn_name.isEmpty()
                || _params.isEmpty()
                || _body.isEmpty()) {
            return fatal("wrong fn syntax: <(fn name (params) (body))>");
        }

        final String fn = isValidSExpr(_fn_name) ? (String) temp_eval(_fn_name, false) : _fn_name;

        if (funcDefines.contains(fn)) {
            fatal("fn defines duplicated: " + fn);
        }
        funcDefines.add(fn);

        final Fn gFn = new Fn(fn, _params, _body, this);
        registerFunctor(fn, gFn);

        return gFn;
    }

    private final Set<String> recordDefines = new HashSet<>();

    private Object eval_record() {
        final String _name = consume_sexpr_or_word();
        consume_whitespace();
        final String _fields = consume_sexpr();
        consume_whitespace();
        consume(')');

        if (_name.isEmpty()
                || _fields.isEmpty()) {
            return fatal("wrong record syntax: <(record name (fields))>");
        }

        final String record = isValidSExpr(_name) ? (String) temp_eval(_name, false) : _name;

        if (recordDefines.contains(record)) {
            fatal("duplicated record definition: " + record);
        }
        recordDefines.add(record);

        final Record sRecord = new Record(record, _fields);

        registerFunctor(record, sRecord);
        return sRecord;
    }

    private Object make_record(String record) {

        consume_whitespace();
        consume('(');
        final ArrayList<Object> values = new ArrayList<>();
        while (peek_char() != ')') {
            values.add(evalInternal());
            consume_whitespace();
        }
        consume(')');
        consume(')');

        final Functor recordMaker = operators.get(record);
        return recordMaker.call(record, values, env, this);
    }

    private Object eval_let() {
        final String _var = consume_sexpr_or_word();
        consume_whitespace();
        final Object _value = consume_sexpr_or_atom();
        consume_whitespace();
        consume(')');

        final String var = isValidSExpr(_var) ? ((String) temp_eval(_var, false)) : _var;

        // TODO-wei: 2018/5/8 check var 定义
        if (KEYWORDS.contains(var)) {
            fatal("can not use keyword as variable name: " + var);
        }

        if (!VAR_PATTERN.matcher(var).matches()) {
            fatal("var: " + var + " is wrong named!");
        }

        final Object value = isValidSExpr(_value) ? temp_eval(((String) _value), false) : _value;

        env.set(var, value);

        return value;
    }

    private Object eval_do() {
        env.pushNewEnv();
        try {
            Object result = None;
            while (peek_char() != ')') {
                result = evalInternal();
                consume_whitespace();
            }

            consume(')');

            return result;
        } finally {
            env.popEnv();
        }
    }

    private Object eval_op(String operator, List<Object> params) {

        final Functor op = operators.get(operator);
        if (op != null) {
            return op.call(operator, params, env, this);
        }

        if (params.isEmpty()) {
            return temp_eval(operator, false);
        }

        if (env.contains(operator)) {
            final Object var = env.get(operator);
            if (var instanceof Functor) {
                return ((Functor) var).call("", params, env, this);
            }
        }

        return fatal("Unsupported operator: " + operator);
    }

    ///////////////////////////////////////////////////////////////////////////
    // atom
    ///////////////////////////////////////////////////////////////////////////

    private Object eval_atom() {

        for (Atom eval : atoms) {
            final Object _atom = eval.evalAtom(se, index);
            if (_atom != null) {
                this.index += eval.getStepForward();
                return _atom;
            }
        }

        return fatal("Unsupported Atom");
    }

    private static final Pattern VAR_PATTERN = Pattern.compile("([\\w$->*&]+)\\b");

    private class VarAtom extends AbsRegexAtom {

        @Override
        protected Pattern createPattern() {
            return VAR_PATTERN;
        }

        @Override
        protected Object handleRegexResult(String se, int index, Matcher matcher) {
            return env.get(matcher.group(1));
        }

        @Override
        public String toString() {
            return "VarAtom";
        }
    }

    private class OpAtom extends AbsRegexAtom {
        @Override
        protected Pattern createPattern() {
            return Pattern.compile("([^\\s]+)");
        }

        @Override
        protected Object handleRegexResult(String se, int index, Matcher matcher) {
            return operators.get(matcher.group(1));
        }

        @Override
        public String toString() {
            return "OpAtom";
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // consume
    ///////////////////////////////////////////////////////////////////////////

    private void consume(char c) {
        assertTrue(peek_char() == c,
                "internal error, expect char: " + c + " in index: " + index + ", but get: " + peek_char());
        read_next();
    }

    private String consume_operator() {
        return consume_word();
    }

    private Object consume_sexpr_or_atom() {
        if (peek_char() == '(') {
            return consume_sexpr();
        } else {
            return eval_atom();
        }
    }

    private String consume_sexpr_or_word() {
        if (peek_char() == '(') {
            return consume_sexpr();
        } else {
            return consume_word();
        }
    }

    private String consume_sexpr() {
        assertTrue(peek_char() == '(', "Internal error, except (");
        final Deque<Character> parenthesis = new ArrayDeque<>();

        final int start = index;

        read_next();
        parenthesis.push('(');

        for (int i = index; i < se.length() && !parenthesis.isEmpty(); i++) {

            if (peek_char() == '(') {
                parenthesis.push('(');
            }

            if (peek_char() == ')') {
                parenthesis.pop();
            }

            read_next();
        }

        final int end = index;
        return se.substring(start, end);
    }

    private String consume_word() {
        final StringBuilder result = new StringBuilder();

        while (index < se.length() &&
                !isEnd(peek_char())) {

            result.append(peek_char());
            read_next();
        }
        return result.toString();
    }

    private char peek_char() {
        return se.charAt(index);
    }

    private void read_next() {
        index++;

        if (index < se.length() && peek_char() == '\n') {
            lineEndMark.add(index);
        }
    }

    private boolean isEnd(char c) {
        return isWhitespace(c) || c == ')';
    }

    private void consume_whitespace() {
        while (index < se.length() - 1 && isWhitespace(peek_char())) {
            read_next();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // diagnose
    ///////////////////////////////////////////////////////////////////////////

    public Object fatal(String msg) {
        print_diagnose_info(msg);

        throw new GaloisException(msg);
    }

    private void print_diagnose_info(String msg) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println();
        System.out.println(msg);

        if (se != null) {
            System.out.println("error occur in index: " + index);
            System.out.println();

            lineEndMark.add(se.length());

            final String[] codeInLines = se.split("\n");

            int lastLineEnd = 0;
            final int errIndex = this.index;
            for (int index = 0; index < codeInLines.length; index++) {
                System.out.println(codeInLines[index]);
                final Integer lineEnd = lineEndMark.get(index);

                if (errIndex <= lineEnd && errIndex > lastLineEnd) {
                    for (int i = 0; i < (errIndex - lastLineEnd - 1); i++) {
                        System.out.print('-');
                    }
                    System.out.print('^');
                }

                lastLineEnd = lineEnd;
            }

            System.out.println();
        }

        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    public Object fatal(String msg, Throwable cause) {
        print_diagnose_info(msg);
        throw new GaloisException(msg, cause);
    }

    public void dumpEnv() {
        System.out.println("env snapshot: ");
        System.out.println();
        env.dump();
        System.out.println();
    }

    public void assertTrue(boolean expression, String msg) {
        if (!expression) {
            fatal(msg);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // helper
    ///////////////////////////////////////////////////////////////////////////

    private Iterable toIterable(Object object) {
        if (object instanceof Iterable) {
            return ((Collection) object);
        }

        if (object.getClass().isArray()) {
            return Arrays.asList(((Object[]) object));
        }

        if (object instanceof String) {
            return new StringIterable(((String) object));
        }

        fatal("not a iterable type: " + object);
        throw new GaloisException("not a iterable type: " + object);
    }

    static void checkParenthesisMatch(String se) {
        final Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < se.length(); i++) {
            final char c = se.charAt(i);
            if (c == '(') {
                stack.push('(');
            }

            if (c == ')') {
                try {
                    stack.pop();
                } catch (Exception e) {
                    throw new GaloisException("parenthesis miss match!");
                }
            }
        }

        if (!stack.isEmpty()) {
            throw new GaloisException("parenthesis miss match!");
        }
    }

    private static boolean isValidSExpr(Object str) {

        if (!(str instanceof String)) {
            return false;
        }

        final String se = (String) str;


        final boolean isSExpr = se.startsWith("(") && se.endsWith(")");
        if (!isSExpr) {
            return false;
        }

        try {
            checkParenthesisMatch(se);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
