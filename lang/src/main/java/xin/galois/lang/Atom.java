package xin.galois.lang;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Atom {

    public interface AtomEval {
        Object eval(String rawStr, EvalEnv env, Matcher matchResult);
    }

    private static final Pattern None = Pattern.compile("None");
    private static final Pattern Bool = Pattern.compile("True|False");
    private static final Pattern Int = Pattern.compile("[+-]?\\d+");
    private static final Pattern IntR16 = Pattern.compile("([+-])?0x([\\da-fA-F]+)");
    private static final Pattern Float = Pattern.compile("[+-]?\\d+\\.\\d+");
    private static final Pattern Symbol = Pattern.compile(":(\\w+)");
    private static final Pattern Var = Pattern.compile("[\\w$#@!?\\->]+");
    private static final Pattern Str = Pattern.compile("'(.*)'");
    private static final Pattern Str2 = Pattern.compile("\"(.*)\"");
    private static final Pattern TemplateStr = Pattern.compile("`(.*)`");

    private static final Map<Pattern, AtomEval> atoms = new LinkedHashMap<Pattern, AtomEval>() {{
        put(None, new NoneAtom());
        put(Bool, new BoolAtom());
        put(Int, new IntAtom());
        put(IntR16, new IntR16Atom());
        put(Float, new FloatAtom());
        put(Symbol, new SymbolAtom());
        put(Str, new StrAtom());
        put(Str2, new StrAtom());
        put(TemplateStr, new PatternStrAtom());
        put(Var, new VarAtom());
    }};

    static Object eval(AstNode node, EvalEnv env) {

        final String atomStr = node.rawStr;

        for (Pattern pat : atoms.keySet()) {
            final Matcher matcher = pat.matcher(atomStr);
            if (matcher.matches()) {
                final AtomEval atomEval = atoms.get(pat);
                return atomEval.eval(atomStr, env, matcher);
            }
        }

        return null;
    }

    static class NoneAtom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return Galois.None;
        }
    }

    static class BoolAtom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return Boolean.valueOf(rawStr);
        }
    }

    static class IntAtom implements AtomEval {

        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return Integer.parseInt(rawStr);
        }
    }

    static class IntR16Atom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            final int sign = Util.equals("-", matchResult.group(1)) ? -1 : 1;
            return sign * Integer.parseInt(matchResult.group(2), 16);
        }
    }

    static class FloatAtom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return Double.parseDouble(rawStr);
        }
    }

    static class SymbolAtom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return matchResult.group(1);
        }
    }

    static class StrAtom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return matchResult.group(1);
        }
    }

    static class PatternStrAtom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return Util.template(matchResult.group(1), env);
        }
    }

    static class VarAtom implements AtomEval {
        @Override
        public Object eval(String rawStr, EvalEnv env, Matcher matchResult) {
            return env.get(rawStr);
        }
    }

}
