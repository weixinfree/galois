package xin.galois.lang;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * 单元测试
 * Created by wangwei on 2018/5/3.
 */
public class GaloisTest {
    @Test
    public void test_quickCheck_normal() throws Exception {
        Galois.checkParenthesisMatch("(())");
        Galois.checkParenthesisMatch("atom");
    }

    @Test(expected = GaloisException.class)
    public void test_quickCheck() throws Exception {
        Galois.checkParenthesisMatch("(()");
    }

    @Test(expected = GaloisException.class)
    public void test_quickCheck3() throws Exception {
        Galois.checkParenthesisMatch("((()((()))(()))))))");
    }

    @Test(expected = GaloisException.class)
    public void test_quickCheck4() throws Exception {
        Galois.checkParenthesisMatch("(");
    }

    @Test(expected = GaloisException.class)
    public void test_quickCheck2() throws Exception {
        Galois.checkParenthesisMatch(")");
    }

    @Test
    public void eval_atom() throws Exception {
        assertEquals(Galois.evalS("1"), 1);
        assertEquals(Galois.evalS("true"), true);
        assertEquals(Galois.evalS("false"), false);
        assertEquals(Galois.evalS("None"), Galois.None);

        assertEquals(Galois.evalS("-1"), -1);
        assertEquals(Galois.evalS("-0"), -0);
        assertEquals(Galois.evalS("0.2"), 0.2);
        assertEquals(Galois.evalS("-0"), 0);
        assertEquals(Galois.evalS("1.0"), 1.0);
        assertEquals(Galois.evalS("-1.0"), -1.0);
        assertEquals(Galois.evalS(":hello"), "hello");
        assertEquals(Galois.evalS(":key"), "key");
        assertEquals(Galois.evalS("'hello world'"), "hello world");
        assertEquals(Galois.evalS("' 1 '"), " 1 ");
        assertEquals(Galois.evalS("''"), "");

        assertEquals(Galois.evalS("(true)"), true);
        assertEquals(Galois.evalS("(1)"), 1);
        assertEquals(Galois.evalS("(:hello)"), "hello");
    }

    @Test(expected = GaloisException.class)
    public void test_atom1() throws Exception {
        Galois.evalS(":");
    }

    @Test
    public void eval_add() throws Exception {
        assertEquals(Galois.evalS("(+ 3 4)"), 7.0);
        assertEquals(Galois.evalS("(+ 3)"), 3.0);
        assertEquals(Galois.evalS("(+)"), 0.0);
        assertEquals(Galois.evalS("(+ 1 2 3 4)"), 10.0);
        assertEquals(Galois.evalS("(+ 1 2 3 4 5)"), 15.0);
    }

    @Test
    public void test_minus() throws Exception {
        assertEquals(Galois.evalS("(- 3 4)"), -1.0);
        assertEquals(Galois.evalS("(-)"), 0.0);
        assertEquals(Galois.evalS("(- 1)"), -1.0);
        assertEquals(Galois.evalS("(- 3 4)"), -1.0);
        assertEquals(Galois.evalS("(- 10 1 1 2)"), 6.0);
    }

    @Test
    public void test_multi() throws Exception {
        assertEquals(Galois.evalS("(*)"), 1.0);
        assertEquals(Galois.evalS("(* 1)"), 1.0);
        assertEquals(Galois.evalS("(* 2)"), 2.0);
        assertEquals(Galois.evalS("(* 10 2)"), 20.0);
        assertEquals(Galois.evalS("(* 10 10 10 3)"), 3000.0);
    }

    @Test(expected = GaloisException.class)
    public void test_divide() throws Exception {
        assertEquals(Galois.evalS("(/)"), 1.0);
    }

    @Test(expected = GaloisException.class)
    public void test_divide1() throws Exception {
        assertEquals(Galois.evalS("(/ 1)"), 1.0);
    }

    @Test
    public void test_divider2() throws Exception {
        assertEquals(Galois.evalS("(/ 1 1)"), 1.0);
        assertEquals(Galois.evalS("(/ 1 2)"), 0.5);
        assertEquals(Galois.evalS("(/ 1 3)"), 1 / 3.0);
        assertEquals(Galois.evalS("(/ 10 2)"), 10 / 2.0);
        assertEquals(Galois.evalS("(/ 10 2 5)"), 10 / 2.0 / 5);
    }

    @Test
    public void test_divide3() throws Exception {
        assertEquals(Galois.evalS("(/ 10 0)"), Double.POSITIVE_INFINITY);
        assertEquals(Galois.evalS("(/ 0 0)"), Double.NaN);
        assertEquals(Galois.evalS("(/ -0 0)"), Double.NaN);
        assertEquals(Galois.evalS("(/ -10 0)"), Double.NEGATIVE_INFINITY);
    }

    @Test
    public void test_mod() throws Exception {
        assertEquals(Galois.evalS("(% 10 3)"), 1);
        assertEquals(Galois.evalS("(% 10 3)"), 1);
        assertEquals(Galois.evalS("(% 10 3)"), 1);
        assertEquals(Galois.evalS("(% 10 3 2)"), 1);
    }

    @Test(expected = ClassCastException.class)
    public void test_mod1() throws Exception {
        assertEquals(Galois.evalS("(% 10.01 3.0)"), 1);
    }

    @Test(expected = GaloisException.class)
    public void test_mod2() throws Exception {
        assertEquals(Galois.evalS("(%)"), 1);
        assertEquals(Galois.evalS("(% 10)"), 1);
    }

    @Test(expected = GaloisException.class)
    public void test_compare_error() throws Exception {
        assertEquals(Galois.evalS("(>)"), true);
        assertEquals(Galois.evalS("(> 3)"), true);
        assertEquals(Galois.evalS("(<)"), true);
        assertEquals(Galois.evalS("(>=)"), true);
        assertEquals(Galois.evalS("(=)"), true);
        assertEquals(Galois.evalS("(==)"), true);
        assertEquals(Galois.evalS("(<=)"), true);
    }

    @Test
    public void test_compare() throws Exception {
        assertEquals(Galois.evalS("(> 3 0)"), true);
        assertEquals(Galois.evalS("(> 3 0 -1)"), true);
        assertEquals(Galois.evalS("(> 3 0 -1 -10)"), true);
        assertEquals(Galois.evalS("(> 3 10)"), false);
        assertEquals(Galois.evalS("(> 3 0 3)"), false);
        assertEquals(Galois.evalS("(> 3.0 0.0 3.0)"), false);

        assertEquals(Galois.evalS("(< 3 0)"), false);
        assertEquals(Galois.evalS("(< 0 3 4)"), true);
        assertEquals(Galois.evalS("(< 0 3 4 5)"), true);
        assertEquals(Galois.evalS("(< 0 3 4 5 1)"), false);

        assertEquals(Galois.evalS("(< 0.0 3.0 4.0 5.0 1.0)"), false);

        assertEquals(Galois.evalS("(== 0 3)"), false);
        assertEquals(Galois.evalS("(= 0 3)"), false);
        assertEquals(Galois.evalS("(= 3 3)"), true);
        assertEquals(Galois.evalS("(== 3 3)"), true);
        assertEquals(Galois.evalS("(== 3 3 3 3)"), true);
        assertEquals(Galois.evalS("(== 3 3 3 4)"), false);

        assertEquals(Galois.evalS("(<= 0 3)"), true);
        assertEquals(Galois.evalS("(<= 0 0)"), true);
        assertEquals(Galois.evalS("(<= 1 2 3 4 5)"), true);


        assertEquals(Galois.evalS("(>= 10 9 8 7 6)"), true);
        assertEquals(Galois.evalS("(>= 10 9)"), true);
    }

    @Test(expected = ClassCastException.class)
    public void test_compare3() throws Exception {
        assertEquals(Galois.evalS("(>= 10.0 9)"), true);
        assertEquals(Galois.evalS("(<= 10.0 9)"), true);
    }

    @Test
    public void test_bool() throws Exception {
        assertEquals(Galois.evalS("(? true)"), true);
        assertEquals(Galois.evalS("(bool true)"), true);
        assertEquals(Galois.evalS("(? false)"), false);
        assertEquals(Galois.evalS("(bool false)"), false);
        assertEquals(Galois.evalS("(bool 0)"), false);
        assertEquals(Galois.evalS("(bool 1)"), true);
        assertEquals(Galois.evalS("(bool 0.0)"), false);
        assertEquals(Galois.evalS("(bool 0.1)"), true);
        assertEquals(Galois.evalS("(? :hello )"), true);
        assertEquals(Galois.evalS("(? '')"), false);
        assertEquals(Galois.evalS("(? 'hello world')"), true);
        assertEquals(Galois.evalS("(? ' ')"), true);
    }

    @Test
    public void test_str() throws Exception {
        assertEquals(Galois.evalS("(str true)"), "true");
        assertEquals(Galois.evalS("(str 1)"), "1");
        assertEquals(Galois.evalS("(str 1.0)"), "1.0");
        assertEquals(Galois.evalS("(str :hello)"), "hello");
        assertEquals(Galois.evalS("(str '' )"), "");
        assertEquals(Galois.evalS("(str 'hello' ' ' 'world' '!')"), "hello world!");
        assertEquals(Galois.evalS("(str 1 2 '-' 3.0)"), "12-3.0");
        assertEquals(Galois.evalS("(str true '-' 1 '-' 2.0)"), "true-1-2.0");
    }

    @Test
    public void test_compose_bool() throws Exception {
        assertEquals(Galois.evalS("(and true false)"), false);
        assertEquals(Galois.evalS("(and true)"), true);
        assertEquals(Galois.evalS("(and false)"), false);

        assertEquals(Galois.evalS("(and 1)"), true);
        assertEquals(Galois.evalS("(and 1 0)"), false);
        assertEquals(Galois.evalS("(and 1 '')"), false);
        assertEquals(Galois.evalS("(and 1 2 3.0)"), true);

        assertEquals(Galois.evalS("(and true true)"), true);
        assertEquals(Galois.evalS("(and false false)"), false);
        assertEquals(Galois.evalS("(and false true)"), false);
        assertEquals(Galois.evalS("(and true true true)"), true);
        assertEquals(Galois.evalS("(and true true true true)"), true);
        assertEquals(Galois.evalS("(and true false true true)"), false);

        assertEquals(Galois.evalS("(or false)"), false);
        assertEquals(Galois.evalS("(or true)"), true);
        assertEquals(Galois.evalS("(or false false false)"), false);
        assertEquals(Galois.evalS("(or false false false true)"), true);

        assertEquals(Galois.evalS("(or 1)"), true);
        assertEquals(Galois.evalS("(or 1 2)"), true);
        assertEquals(Galois.evalS("(or 0 0 '')"), false);


        assertEquals(Galois.evalS("(not 0)"), true);
        assertEquals(Galois.evalS("(not false)"), true);
        assertEquals(Galois.evalS("(not true)"), false);
    }

    @Test(expected = GaloisException.class)
    public void test_and_fail() throws Exception {
        Galois.evalS("(and)");
    }

    @Test(expected = GaloisException.class)
    public void test_or_fail() throws Exception {
        Galois.evalS("(or)");
    }

    @Test(expected = GaloisException.class)
    public void test_not_failed() throws Exception {
        Galois.evalS("(not)");
    }

    @Test(expected = GaloisException.class)
    public void test_not_failed2() throws Exception {
        Galois.evalS("(not true true)");
    }

    @Test
    public void test_let() throws Exception {
        final Galois eval = new Galois();
        assertEquals(eval.eval("(let name :xiaoming)"), "xiaoming");
        assertEquals(eval.eval("name"), "xiaoming");
        assertEquals(eval.eval("(let name :xm)"), "xm");
        assertEquals(eval.eval("name"), "xm");
        assertEquals(eval.eval("(let age 10)"), 10);
        assertEquals(eval.eval("(age)"), 10);
    }

    @Test(expected = GaloisException.class)
    public void test_def_fail() throws Exception {
        Galois.evalS("(let if 1)");
    }

    @Test
    public void test_int() throws Exception {
        assertEquals(Galois.evalS("(int 3)"), 3);
        assertEquals(Galois.evalS("(int 3.2)"), 3);
        assertEquals(Galois.evalS("(int '3')"), 3);
        assertEquals(Galois.evalS("(int 'F' 16)"), 15);
        assertEquals(Galois.evalS("(int '17' 8)"), 15);
        assertEquals(Galois.evalS("(int '11' 2)"), 3);
    }

    @Test(expected = NumberFormatException.class)
    public void test_int_failed() throws Exception {
        assertEquals(Galois.evalS("(int '3.2')"), 3);
    }

    @Test(expected = GaloisException.class)
    public void test_int_failed2() throws Exception {
        Galois.evalS("(int 3 2)");
    }

    @Test(expected = GaloisException.class)
    public void test_int_failed3() throws Exception {
        Galois.evalS("(int '3' 2 3)");
    }

    @Test(expected = ClassCastException.class)
    public void test_int_failed4() throws Exception {
        Galois.evalS("(int '3' 2.3)");
    }

    @Test
    public void test_dot() throws Exception {
        final Galois eval = new Galois();
        assertEquals(eval.eval("(. Integer :parseInt '3')"), 3);
        assertEquals(eval.eval("(. 10 :parseInt '3')"), 3);
        assertEquals(eval.eval("(. '' :isEmpty)"), true);
        assertEquals(eval.eval("(. ' ' :isEmpty)"), false);
        assertEquals(eval.eval("(. String :format 'age: %d, name: %s' (array 10 'xm'))"), "age: 10, name: xm");

        assertEquals(eval.eval("(. 'hello world' :substring 0 5)"), "hello");
    }

    @Test
    public void test_array() throws Exception {
        assertArrayEquals((Object[]) Galois.evalS("(array)"), new Object[]{});
        assertArrayEquals((Object[]) Galois.evalS("(array 1)"), new Object[]{1});
        assertArrayEquals((Object[]) Galois.evalS("(array 1 2.2)"), new Object[]{1, 2.2});
        assertArrayEquals((Object[]) Galois.evalS("(array 1 2.2 '3.3')"), new Object[]{1, 2.2, "3.3"});
        assertEquals(Galois.evalS("(len (array 1 2.2 '3.3'))"), 3);
    }

    @Test
    public void test_len() throws Exception {
        assertEquals(Galois.evalS("(len (array 1))"), 1);
        assertEquals(Galois.evalS("(len (array))"), 0);
        assertEquals(Galois.evalS("(len (array 1 2))"), 2);

        assertEquals(Galois.evalS("(len (list 1 2))"), 2);
        assertEquals(Galois.evalS("(len (list))"), 0);

        assertEquals(Galois.evalS("(len (set 1 2))"), 2);
        assertEquals(Galois.evalS("(len (set 1 1))"), 1);

        assertEquals(Galois.evalS("(len 'hello world')"), 11);
    }

    @Test(expected = GaloisException.class)
    public void test_len_failed() throws Exception {
        Galois.evalS("(len)");
    }

    @Test(expected = GaloisException.class)
    public void test_len_failed2() throws Exception {
        Galois.evalS("(len 1)");
        Galois.evalS("(len 'hello')");
        Galois.evalS("(len 3.2)");
        Galois.evalS("(len none)");
    }

    @Test
    public void test_list() throws Exception {
        assertEquals(Galois.evalS("(list)"), new ArrayList<>());

        assertEquals(Galois.evalS("(list 1)"), new ArrayList<Integer>() {{
            add(1);
        }});

        assertEquals(Galois.evalS("(list 1 2 3)"), new ArrayList<Object>() {{
            add(1);
            add(2);
            add(3);
        }});

        assertEquals(Galois.evalS("(list 1  'hello' 3.2)"), new ArrayList<Object>() {{
            add(1);
            add("hello");
            add(3.2);
        }});

        assertEquals(Galois.evalS("(. (list 1  'hello' 3.2) :size)"), 3);

        assertEquals(Galois.evalS("(list 1 2 2 3 3 3)"), new ArrayList<Object>() {{
            add(1);
            add(2);
            add(2);
            add(3);
            add(3);
            add(3);
        }});


        assertEquals(Galois.evalS("(. (list 1 2 2 3 3 3) :size)"), 6);
    }

    @Test
    public void test_set() throws Exception {

        assertEquals(Galois.evalS("(set)"), new HashSet<>());

        assertEquals(Galois.evalS("(set 1)"), new HashSet<Object>() {{
            add(1);
        }});

        assertEquals(Galois.evalS("(set 1 2 3)"), new HashSet<Object>() {{
            add(1);
            add(2);
            add(3);
        }});

        assertEquals(Galois.evalS("(set 1 2 2 3 3 3)"), new HashSet<Object>() {{
            add(1);
            add(2);
            add(3);
        }});

        assertEquals(Galois.evalS("(set 1  'hello' 3.2)"), new HashSet<Object>() {{
            add(1);
            add("hello");
            add(3.2);
        }});

        assertEquals(Galois.evalS("(. (set 1  'hello' 3.2) :size)"), 3);
        assertEquals(Galois.evalS("(. (set 1 2 2 3 3 3) :size)"), 3);
    }

    @Test
    public void test_if() throws Exception {
        assertEquals(Galois.evalS("(if (> 3 0) true false)"), true);
        assertEquals(Galois.evalS("(if true true false)"), true);
        assertEquals(Galois.evalS("(if false true false)"), false);
        assertEquals(Galois.evalS("(if (> 3 0) (true) (false))"), true);
        assertEquals(Galois.evalS("(if (< 3 0) (true) (false))"), false);

        // TODO-wei: 2018/5/4
        assertEquals(Galois.evalS("(if (< 3 0) (println true) false)"), false);
        assertEquals(Galois.evalS("(if (< 3 0) (println true) false)"), false);
    }

    @Test
    public void test_if_nest() throws Exception {

    }

    @Test(expected = GaloisException.class)
    public void test_if_scope() throws Exception {
        final Galois eval = new Galois();
        eval.eval("(do (if true (def key 1) (def key 2)) key)");
    }

    @Test(expected = GaloisException.class)
    public void test_if_fail1() throws Exception {
        Galois.evalS("(if)");
    }

    @Test(expected = GaloisException.class)
    public void test_if_fail2() throws Exception {
        Galois.evalS("(if 1)");
    }

    @Test(expected = GaloisException.class)
    public void test_if_fail3() throws Exception {
        Galois.evalS("(if 1 true)");
    }

    @Test
    public void test_do() throws Exception {
        assertEquals(Galois.evalS("(do)"), Galois.None);
        assertEquals(Galois.evalS("(do true)"), true);
        assertEquals(Galois.evalS("(do (> 3 0) 1)"), 1);
        assertEquals(Galois.evalS("(do (1) 13 (str 12 '-' 13))"), "12-13");
        assertEquals(Galois.evalS("(do (if true 1 (0)))"), 1);
        assertEquals(Galois.evalS("(do 1 (str 2 '-' 13) (let name :xm) name)"), "xm");
        assertEquals(Galois.evalS("(do (let l1 1) (do (let l1 2)) l1)"), 1);
    }

    @Test
    public void test_do_nest() throws Exception {

    }

    @Test
    public void test_compose_sexpr() throws Exception {
        final String s = "(do " +
                "(let name :xm)" +
                "(let age 10)" +
                "(if (> age 18) " +
                "   (println (str name ' ' 'age = ' age ' is adult!'))" +
                "   (println (str name ' ' 'age = ' age ' is teenager!' )))" +
                ")";

        Galois.evalS(s);
    }

    @Test(expected = GaloisException.class)
    public void test_do_scope() throws Exception {
        final Galois eval = new Galois();
        eval.eval("(do (let l1 1)" +
                "            (do (let l2 2) (+ l1 l2))" +
                "            l2)");
    }

    @Test
    public void test_iter() throws Exception {
        final Galois eval = new Galois();
        eval.eval("(iter (list 1 2 3) (println $index $it))");
        eval.eval("(iter (array 1 2 3) (println $index $it))");
        eval.eval("(iter (set 1 2 3) (println $index $it))");
        eval.eval("(iter 'hello world' (println $index $it))");
    }

    @Test
    public void test_iter_nest() throws Exception {

    }

    @Test(expected = GaloisException.class)
    public void test_iter_fail1() throws Exception {
        Galois.evalS("(iter)");
    }

    @Test
    public void test_fn() throws Exception {
        assertEquals(Galois.evalS("(do (fn add (x y) (+ x y)) (add 1 2))"), 3.0);
        assertEquals(Galois.evalS("(do (fn add () (3)) (add))"), 3);
        assertEquals(Galois.evalS("(do (fn add (x y) (int (+ x y))) (add 1 2))"), 3);
        assertEquals(Galois.evalS("(do (let z 10) (fn add (x y) (+ x y z)) (add 1 2))"), 13.0);
    }

    @Test(expected = GaloisException.class)
    public void test_fn_syntax_fail1() throws Exception {
        Galois.evalS("(fn)");
    }

    @Test(expected = GaloisException.class)
    public void test_fn_syntax_fail2() throws Exception {
        Galois.evalS("(fn add)");
    }

    @Test(expected = GaloisException.class)
    public void test_fn_syntax_fail3() throws Exception {
        Galois.evalS("(fn add (x y))");
    }

    @Test(expected = GaloisException.class)
    public void test_fn_syntax_fail4() throws Exception {
        Galois.evalS("(do (fn add () ()) (add 1))");
    }

    @Test(expected = GaloisException.class)
    public void test_fn_syntax_fail5() throws Exception {
        Galois.evalS("(do (fn add (x) ()) (add))");
    }

    @Test
    public void test_fn_nest() throws Exception {

    }

    @Test
    public void test_fn_recur() throws Exception {

    }

    @Test(expected = GaloisException.class)
    public void test_fn_scope() throws Exception {
        Galois.evalS("(do (fn add (x y) (+ x y)) (add 2 3) x)");
    }

    @Test
    public void test_if_syntax_fail() throws Exception {

    }

    @Test
    public void test_let_syntax_fail() throws Exception {

    }

    @Test
    public void test_iter_syntax_fail() throws Exception {

    }

    @Test
    public void test_dict() throws Exception {
        assertEquals(Galois.evalS("(do (let book-authors (dict 'xm' '长征' 'xh' '文革')) (book-authors :xm))"), "长征");
        assertEquals(Galois.evalS("(do (let d (dict '1' 1 '2' 2)) (d '2' 3) (d '2'))"), 3);
        assertEquals(Galois.evalS("(do (let d (dict '1' 1 '2' 2)) (d '3' 3) (d '3'))"), 3);

        final HashMap<Object, Object> map = new HashMap<>();
        map.put(1, "int");
        map.put("str", "hello world");
        map.put(3.0, "float");
        assertEquals(Galois.evalS("(do (let d (dict  1 'int' 'str' 'hello world' 3.0 'float')))"), map);

        assertEquals(Galois.evalS("(dict)"), new HashMap<>());
    }

    @Test(expected = GaloisException.class)
    public void test_dict_fail() throws Exception {
        Galois.evalS("(dict 1)");
    }

    @Test(expected = GaloisException.class)
    public void test_dict_fail2() throws Exception {
        Galois.evalS("(dict 1 2 3)");
    }

    @Test
    public void test_record() throws Exception {
        assertEquals(Galois.evalS("(record User (name age sex))"), new Record("User", "(name age sex)"));
        assertEquals(Galois.evalS("(do " +
                "(record User (name age sex)) " +
                "(let xm " +
                "(User ('xm' 10 'male'))) " +
                "(xm :age))"), 10);
    }

    @Test
    public void test_code_file() throws Exception {
        final File file = new File("/Users/wangwei/AndroidStudioProjects/Galois/lang/src/test/java/xin/galois/lang/code.gal");

        final char[] chars = new char[1024];

        final Reader reader = new BufferedReader(new FileReader(file));
        final int count = reader.read(chars);
        reader.close();

        final String code = new String(chars, 0, count);
        Galois.evalS(code);
    }

    @Test
    public void test_assert() throws Exception {

    }
}