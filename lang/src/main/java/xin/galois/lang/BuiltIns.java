package xin.galois.lang;

import xin.galois.lang.atom.FalseAtom;
import xin.galois.lang.atom.FloatAtom;
import xin.galois.lang.atom.IntAtom;
import xin.galois.lang.atom.NoneAtom;
import xin.galois.lang.atom.StrAtom;
import xin.galois.lang.atom.SymbolAtom;
import xin.galois.lang.atom.TrueAtom;
import xin.galois.lang.builtin.BasicBoolOp;
import xin.galois.lang.builtin.BasicDataTypesOp;
import xin.galois.lang.builtin.BasicJavaInterpretiveOp;
import xin.galois.lang.builtin.BasicMathOp;

/**
 * builtin 环境变量
 * Created by wangwei on 2018/5/5.
 */

class BuiltIns {

    static void installFunctor(Galois eval) {
        eval.registerFunctor("+", new BasicMathOp.AddOp());
        eval.registerFunctor("add", new BasicMathOp.AddOp());
        eval.registerFunctor("-", new BasicMathOp.MinusOp());
        eval.registerFunctor("minus", new BasicMathOp.MinusOp());
        eval.registerFunctor("*", new BasicMathOp.MultiOp());
        eval.registerFunctor("multi", new BasicMathOp.MultiOp());
        eval.registerFunctor("/", new BasicMathOp.DivideOp());
        eval.registerFunctor("divide", new BasicMathOp.DivideOp());
        eval.registerFunctor("%", new BasicMathOp.ModOp());
        eval.registerFunctor("mod", new BasicMathOp.ModOp());

        eval.registerFunctor(">", new BasicBoolOp.GTOp());
        eval.registerFunctor(">=", new BasicBoolOp.GEOp());
        eval.registerFunctor("<", new BasicBoolOp.LTOp());
        eval.registerFunctor("<=", new BasicBoolOp.LEOp());
        eval.registerFunctor("=", new BasicBoolOp.EQOp());

        eval.registerFunctor("bool", new BasicBoolOp.BoolOp());
        eval.registerFunctor("?", new BasicBoolOp.BoolOp());

        eval.registerFunctor("and", new BasicBoolOp.AndOp());
        eval.registerFunctor("or", new BasicBoolOp.OrOp());
        eval.registerFunctor("not", new BasicBoolOp.NotOp());

        eval.registerFunctor("==", new BasicBoolOp.EqualsOp());
        eval.registerFunctor("!=", new BasicBoolOp.NoEqualsOp());

        eval.registerFunctor("new", new BasicJavaInterpretiveOp.NewOp());
        eval.registerFunctor("class", new BasicJavaInterpretiveOp.ClassOp());
        eval.registerFunctor(".", new BasicJavaInterpretiveOp.DotOp());
        eval.registerFunctor("print", new BasicJavaInterpretiveOp.PrintOp());
        eval.registerFunctor("println", new BasicJavaInterpretiveOp.PrintlnOp());
        eval.registerFunctor("assert", new BasicJavaInterpretiveOp.AssertOp());

        eval.registerFunctor("str", new BasicDataTypesOp.StrOp());
        eval.registerFunctor("int", new BasicDataTypesOp.IntOp());

        eval.registerFunctor("array", new BasicDataTypesOp.ArrayOp());
        eval.registerFunctor("list", new BasicDataTypesOp.ListOp());
        eval.registerFunctor("set", new BasicDataTypesOp.SetOp());
        eval.registerFunctor("dict", new BasicDataTypesOp.DictOp());
        eval.registerFunctor("keys", new BasicDataTypesOp.KeysOp());
        eval.registerFunctor("values", new BasicDataTypesOp.ValuesOp());
        eval.registerFunctor("len", new BasicDataTypesOp.LenOp());
    }

    static void installAtoms(Galois eval) {
        eval.registerAtom(new NoneAtom());

        eval.registerAtom(new FalseAtom());
        eval.registerAtom(new TrueAtom());

        eval.registerAtom(new FloatAtom());

        eval.registerAtom(new IntAtom.R2());
        eval.registerAtom(new IntAtom.R8());
        eval.registerAtom(new IntAtom.R10());
        eval.registerAtom(new IntAtom.R16());

        eval.registerAtom(new StrAtom());
        eval.registerAtom(new SymbolAtom());
    }

}
