package xin.galois.lang;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wangwei on 2018/5/12.
 */
public class AtomTest {

    private EvalEnv env;

    @Before
    public void setUp() throws Exception {
        env = new EvalEnv();
    }

    private static AstNode node(String raw) {
        return new AstNode(AstNode.Type.ATOM, raw);
    }

    @Test
    public void evalBool() throws Exception {
        assertEquals(Atom.eval(node("True"), env), Boolean.TRUE);
        assertEquals(Atom.eval(node("False"), env), Boolean.FALSE);
    }

    @Test
    public void evalInt() throws Exception {
        assertEquals(Atom.eval(node("10"), env), 10);
        assertEquals(Atom.eval(node("1000"), env), 1000);
        assertEquals(Atom.eval(node("-1000"), env), -1000);
        assertEquals(Atom.eval(node("-0"), env), -0);
        assertEquals(Atom.eval(node("-0x100"), env), -0x100);
        assertEquals(Atom.eval(node("-0x0"), env), -0x0);
        assertEquals(Atom.eval(node("0x01"), env), 0x01);
        assertEquals(Atom.eval(node("0xFFddff"), env), 0xFFddff);
        assertEquals(Atom.eval(node("0x22FF88FF"), env), 0x22FF88FF);
    }

    @Test
    public void evalSymbol() throws Exception {
        assertEquals(Atom.eval(node(":hello"), env), "hello");
    }

    @Test
    public void evalStr() throws Exception {
        assertEquals(Atom.eval(node("'hello world!\''"), env), "hello world!'");
    }

    @Test
    public void evalStr2() throws Exception {
        assertEquals(Atom.eval(node("\"hello\" world!\""), env), "hello\" world!");
    }

    @Test
    public void evalTemplateStr() throws Exception {
        env.set("name", "xm");
        env.set("age", 10);
        assertEquals(Atom.eval(node("`name: ${name}, age: ${age}`"), env), "name: xm, age: 10");
    }

    @Test
    public void evalFloat() throws Exception {
        assertEquals(Atom.eval(node("3.2"), env), 3.2);
        assertEquals(Atom.eval(node("-3.2"), env), -3.2);
        assertEquals(Atom.eval(node("-10.3"), env), -10.3);
        assertEquals(Atom.eval(node("-0.0"), env), -0.0);
        assertEquals(Atom.eval(node("0.0"), env), 0.0);
    }

    @Test
    public void evalVar() throws Exception {
        env.let("xm", "xiaoming");
        assertEquals(Atom.eval(node("xm"), env), "xiaoming");
    }

    @Test
    public void test() throws Exception {
        System.out.println(int.class);
        System.out.println(Integer.class);
        System.out.println(Integer.class.isAssignableFrom(int.class));
        System.out.println(int.class.isAssignableFrom(Integer.class));
        System.out.println(int.class == Integer.class);
        System.out.println(int[].class.getComponentType() == int.class);
    }
}