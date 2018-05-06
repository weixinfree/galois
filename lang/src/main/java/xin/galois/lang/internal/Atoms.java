package xin.galois.lang.internal;

import xin.galois.lang.Atom;
import xin.galois.lang.Galois;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内置Atom
 * Created by wangwei on 2018/5/3.
 */

public class Atoms {

    private Atoms() {
        //no instance
    }

    public static class NoneAtom implements Atom {
        @Override
        public Object evalAtom(String atom) {
            if (atom.equals("None")) {
                return Galois.None;
            }
            return null;
        }
    }

    public static class BoolAtom implements Atom {
        @Override
        public Object evalAtom(String atom) {

            if ("true".equals(atom)) {
                return true;
            }

            if ("false".equals(atom)) {
                return false;
            }

            return null;
        }
    }

    public static class IntAtom implements Atom {

        private static final Pattern ATOM_INT = Pattern.compile("[+-]?\\d+");

        @Override
        public Object evalAtom(String atom) {
            if (ATOM_INT.matcher(atom).matches()) {
                return Integer.parseInt(atom);
            }
            return null;
        }
    }

    public static class FloatAtom implements Atom {

        private static final Pattern ATOM_FLOAT = Pattern.compile("[+-]?\\d+\\.\\d*");

        @Override
        public Object evalAtom(String atom) {
            if (ATOM_FLOAT.matcher(atom).matches()) {
                return Double.parseDouble(atom);
            }
            return null;
        }
    }

    public static class StrAtom implements Atom {

        private static final Pattern ATOM_STR = Pattern.compile("'(.*?)'");

        @Override
        public Object evalAtom(String atom) {
            final Matcher matchStr = ATOM_STR.matcher(atom);
            if (matchStr.matches()) {
                return matchStr.group(1);
            }
            return null;
        }
    }

    public static class SymbolAtom implements Atom {

        private static final Pattern ATOM_SYMBOL = Pattern.compile(":([\\w.]+)");

        @Override
        public Object evalAtom(String atom) {

            final Matcher matchSymbol = ATOM_SYMBOL.matcher(atom);
            if (matchSymbol.matches()) {
                return matchSymbol.group(1);
            }

            return null;
        }
    }
}
