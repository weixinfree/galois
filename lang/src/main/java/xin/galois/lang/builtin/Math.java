package xin.galois.lang.builtin;

import java.util.List;

import xin.galois.lang.Functor;
import xin.galois.lang.Galois;

import static xin.galois.lang.Util.asInt;
import static xin.galois.lang.Util.asNum;
import static xin.galois.lang.Util.assertTrue;

public class Math {

    public static void load(Galois galois) {
        galois.registerFunctor("add", new Add());
        galois.registerFunctor("minus", new Minus());
        galois.registerFunctor("mod", new Mod());
        galois.registerFunctor("multi", new Multi());
        galois.registerFunctor("divide", new Divide());
        galois.registerFunctor("abs", new Abs());
    }

    public static class Add extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> args, Galois galois) {
            Number sum = 0;

            for (Object param : args) {

                final Number value = asNum(param);

                if (param instanceof Integer && sum instanceof Integer) {
                    sum = sum.intValue() + value.intValue();
                    continue;
                }

                sum = sum.doubleValue() + value.doubleValue();
            }

            return sum;
        }
    }

    public static class Minus extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {

            final Number value = asNum(params.get(0));

            Number result;
            if (value instanceof Integer) {
                result = value.intValue();
            } else {
                result = value.doubleValue();
            }

            for (int i = 1; i < params.size(); i++) {
                final Number num = asNum(params.get(i));

                if (result instanceof Integer && num instanceof Integer) {
                    result = result.intValue() - num.intValue();
                    continue;
                }

                result = result.doubleValue() - num.doubleValue();
            }

            return result;
        }
    }

    public static class Divide extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            assertTrue(params.size() >= 2, "divide operator need at least 2 args, but given: " + params);

            final Number num = asNum(params.get(0));

            Number result;
            if (num instanceof Integer) {
                result = num.intValue();
            } else {
                result = num.doubleValue();
            }

            for (int i = 1; i < params.size(); i++) {
                final Number value = asNum(params.get(i));

                if (value instanceof Integer && result instanceof Integer) {
                    result = result.intValue() / value.intValue();
                    continue;
                }

                result = result.doubleValue() / value.doubleValue();
            }

            return result;
        }
    }

    public static class Multi extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            Number result = 1;
            for (Object param : params) {
                final Number num = asNum(param);

                if (result instanceof Integer && num instanceof Integer) {
                    result = result.intValue() * num.intValue();
                    continue;
                }

                result = result.doubleValue() * num.doubleValue();
            }

            return result;
        }
    }

    public static class Mod extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            assertTrue(params.size() >= 2, "mod functor need at least 2 args, but given: " + params);

            int remind = asInt(params.get(0));

            for (int i = 1; i < params.size(); i++) {
                remind %= asInt(params.get(i));
            }

            return remind;
        }
    }

    public static class Abs extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> args, Galois galois) {
            assertTrue(args.size() == 1, "abs functor accept exactly 1 args, but given: " + args);

            final Number num = asNum(args.get(0));
            if (num instanceof Integer) {
                return java.lang.Math.abs(num.intValue());
            } else {
                return java.lang.Math.abs(num.doubleValue());
            }
        }
    }
}
