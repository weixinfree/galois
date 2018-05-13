package xin.galois.lang;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import xin.galois.lang.builtin.Bool;
import xin.galois.lang.builtin.DataStructure;
import xin.galois.lang.builtin.Math;

/**
 * 优化版本：先转化为ast，然后针对ast求值
 * Created by wangwei on 2018/5/10.
 */

@SuppressWarnings("WeakerAccess")
public class Galois {

    // TODO-wei: 2018/5/13 REPL

    public static final Object None = new Object() {
        @Override
        public String toString() {
            return "None";
        }
    };

    // internal usage
    static final Object UnDefined = new Object() {
        @Override
        public String toString() {
            return "Undefined";
        }
    };

    @SuppressLint("UseSparseArrays")
    private static final Map<Integer, AstNode> sCodeCache = new HashMap<>();

    private final Map<String, Functor> functorTable;
    final EvalEnv env;

    public Galois() {
        functorTable = new HashMap<>();
        env = new EvalEnv();
        init();
    }

    private Galois(Map<String, Functor> functorTable, EvalEnv env) {
        this.functorTable = functorTable;
        this.env = env;
    }

    public Galois snapshot() {
        return new Galois(new HashMap<>(functorTable), env.snapshot());
    }

    public void registerFunctor(String name, Functor functor) {
        if (functorTable.containsKey(name)) {
            throw new EvalException("duplicated fn define: " + name + ", func: " + functor);
        }

        functorTable.put(name, functor);
    }

    private void init() {
        Math.load(this);
        Bool.load(this);
        DataStructure.load(this);
    }

    @SuppressWarnings("unused")
    public Object eval(String se) {

        AstNode ast = null;

        final int key = se.hashCode();
        if (sCodeCache.containsKey(key)) {
            ast = sCodeCache.get(key);
        }

        if (ast == null) {
            ast = new Parser(se).parse();
        }

        sCodeCache.put(key, ast);
        return eval(ast);
    }

    public Object eval(AstNode ast) {
        if (ast.type == AstNode.Type.ATOM) {
            return evalAtom(ast);
        } else {
            final List<AstNode> children = ast.children;
            if (children.isEmpty()) {
                return None;
            }

            final int size = children.size();
            if (size == 1 && children.get(0).type == AstNode.Type.ATOM) {
                return evalAtom(children.get(0));
            }

            final AstNode opNode = children.get(0);
            final List<AstNode> params = size == 1 ? Collections.<AstNode>emptyList() : children.subList(1, size);

            return evalOp(opNode, params);
        }
    }

    private Object evalOp(AstNode op, List<AstNode> params) {

        final Object evaluatedOp = eval(op);

        if (evaluatedOp instanceof Functor) {
            return ((Functor) evaluatedOp).call("", params, this);
        }

        final String operator = ((String) evaluatedOp);
        switch (operator) {
            case "do": {
                return evalDo(params);
            }
            case "let": {
                return evalLet(params);
            }
            case "set!": {
                return evalSet(params);
            }
            case "if": {
                return evalIf(params);
            }
            case "iter": {
                return evalIter(params);
            }
            case "fn": {
                return evalFn(params);
            }
            default: {

                final Object obj = JavaInterop.interop(operator, params, this);
                if (obj != null) {
                    return obj;
                }

                final Functor functor = functorTable.get(operator);
                if (functor == null) {
                    throw new EvalException("unsupported operator: " + operator);
                }

                return functor.call(operator, params, this);
            }
        }
    }

    private Object evalFn(List<AstNode> params) {
        // anonymous function
        if (params.size() == 2) {

            final List<String> args = toArgs(params.get(0).children);
            final AstNode block = params.get(1);

            final Fn fn = Fn.newAnonymousFn(args, block, this);
            registerFunctor(fn.signature(), fn);
            return fn;
        }

        Util.assertTrue(params.size() == 3, "");
        final String fn_name = ((String) eval(params.get(0)));
        final List<String> args = toArgs(params.get(1).children);
        final AstNode block = params.get(2);

        final Fn functor = Fn.newNamedFn(fn_name, args, block, this);
        registerFunctor(functor.signature(), functor);
        return functor;
    }

    private Object evalIter(List<AstNode> params) {
        Util.assertTrue(params.size() == 2, "");
        final Iterator iterator = Util.asIterable(eval(params.get(0))).iterator();

        final AstNode block = params.get(1);

        env.pushEnv();
        try {

            Object result = None;
            int index = 0;
            while (iterator.hasNext()) {
                final Object value = iterator.next();
                env.let("$index", index);
                env.let("$it", value);

                result = eval(block);
            }

            return result;
        } finally {
            env.popEnv();
        }
    }

    private Object evalIf(List<AstNode> params) {
        Util.assertTrue(params.size() >= 2, "");
        final AstNode test = params.get(0);
        if (Util.bool(eval(test))) {
            final AstNode trueBlock = params.get(1);
            env.pushEnv();
            try {
                return eval(trueBlock);
            } finally {
                env.popEnv();
            }
        } else {
            final AstNode falseBlock = params.get(2);
            env.pushEnv();
            try {
                return eval(falseBlock);
            } finally {
                env.popEnv();
            }
        }
    }

    private Object evalSet(List<AstNode> params) {
        Util.assertTrue(params.size() == 2, "");
        final String var_name = (String) eval(params.get(0));
        final Object value = eval(params.get(1));
        env.set(var_name, value);
        return value;
    }

    private Object evalLet(List<AstNode> params) {
        Util.assertTrue(params.size() == 2, "");
        final String var_name = (String) eval(params.get(0));
        final Object value = eval(params.get(1));
        env.let(var_name, value);
        return value;
    }

    private Object evalDo(List<AstNode> params) {
        env.pushEnv();
        try {
            Object result = None;
            for (AstNode statement : params) {
                result = eval(statement);
            }

            return result;
        } finally {
            env.popEnv();
        }
    }

    @NonNull
    private List<String> toArgs(List<AstNode> _fn_args) {
        final List<String> args = new ArrayList<>(_fn_args.size());
        for (AstNode arg : _fn_args) {
            args.add(((String) evalAtom(arg)));
        }
        return args;
    }

    private Object evalAtom(AstNode node) {
        final Object atom = Atom.eval(node, env);
        if (atom != null && atom != UnDefined) {
            return atom;
        }

        if (functorTable.containsKey(node.rawStr)) {
            return functorTable.get(node.rawStr);
        }

        throw new EvalException("unsupported atom: " + node);
    }
}
