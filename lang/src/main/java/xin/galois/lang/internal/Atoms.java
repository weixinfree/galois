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
                return Boolean.TRUE;
            }

            if ("false".equals(atom)) {
                return Boolean.FALSE;
            }

            return null;
        }
    }

    public static class IntAtom implements Atom {

        private static final Pattern ATOM_INT = Pattern.compile("([+-])?([0-9]+)");
        private static final Pattern ATOM_INT_R2 = Pattern.compile("([+-])?0b([01]+)");
        private static final Pattern ATOM_INT_R8 = Pattern.compile("([+-])?0o([0-7]+)");
        private static final Pattern ATOM_INT_R16 = Pattern.compile("([+-])?0x([0-9a-fA-F]+)");

        @Override
        public Object evalAtom(String atom) {
            if (ATOM_INT.matcher(atom).matches()) {
                return Integer.parseInt(atom);
            }

            {
                final Matcher matcher = ATOM_INT_R16.matcher(atom);
                if (matcher.matches()) {
                    final int sign = getSign(matcher.group(1));
                    final String num = matcher.group(2);
                    return sign * Integer.parseInt(num, 16);
                }
            }

            {
                final Matcher matcher = ATOM_INT_R8.matcher(atom);
                if (matcher.matches()) {
                    final int sign = getSign(matcher.group(1));
                    final String num = matcher.group(2);
                    return sign * Integer.parseInt(num, 8);
                }
            }

            {
                final Matcher matcher = ATOM_INT_R2.matcher(atom);
                if (matcher.matches()) {
                    final int sign = getSign(matcher.group(1));
                    final String num = matcher.group(2);
                    return sign * Integer.parseInt(num, 2);
                }
            }
            return null;
        }

        private int getSign(String sign) {
            if ("-".equals(sign)) {
                return -1;
            }

            return 1;
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

        private static final Pattern ATOM_SYMBOL = Pattern.compile(":(\\w+)");

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
