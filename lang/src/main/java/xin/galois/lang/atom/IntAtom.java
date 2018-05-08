package xin.galois.lang.atom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by wangwei on 2018/5/8.
 */
public class IntAtom {

    abstract static class AbsRadixAtom extends AbsRegexAtom {
        @Override
        protected final Object handleRegexResult(String se, int index, Matcher matcher) {
            final String sign = matcher.group(1);
            final String num = matcher.group(2);

            return sign(sign) * Integer.parseInt(num, getRadix());
        }

        protected abstract int getRadix();
    }

    public static class R2 extends AbsRadixAtom {
        @Override
        protected Pattern createPattern() {
            return Pattern.compile("([+-])?0b([01]+)\\b");
        }

        @Override
        protected int getRadix() {
            return 2;
        }

        @Override
        public String toString() {
            return "IntR2Atom";
        }
    }

    public static class R8 extends AbsRadixAtom {
        @Override
        protected Pattern createPattern() {
            return Pattern.compile("([+-])?0o([0-7]+)\\b");
        }

        @Override
        protected int getRadix() {
            return 8;
        }

        @Override
        public String toString() {
            return "IntR8Atom";
        }
    }

    public static class R10 extends AbsRadixAtom {
        @Override
        protected Pattern createPattern() {
            return Pattern.compile("([+-])?([0-9]+)\\b");
        }

        @Override
        protected int getRadix() {
            return 10;
        }

        @Override
        public String toString() {
            return "IntR10Atom";
        }
    }

    public static class R16 extends AbsRadixAtom {
        @Override
        protected Pattern createPattern() {
            return Pattern.compile("([+-])?0x([0-9a-fA-F]+)\\b");
        }

        @Override
        protected int getRadix() {
            return 16;
        }

        @Override
        public String toString() {
            return "IntR16Atom";
        }
    }

    private static int sign(String sign) {
        if ("-".equals(sign)) {
            return -1;
        }

        return 1;
    }
}
