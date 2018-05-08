package xin.galois.lang.builtin;

import java.util.List;

import xin.galois.lang.Env;
import xin.galois.lang.Functor;
import xin.galois.lang.Galois;

/**
 * 基本的数学运算
 * <p>
 * (+ 1 2)
 * (- 1 2)
 * (* 1 2)
 * (/ 1 2)
 * (% 1 2)
 * <p>
 * todo
 * <p>
 * (sqrt 1 2)
 * (log2 1 2)
 * (log10 1 2)
 * (log 1 2)
 * (exp 1 2)
 * <p>
 * Created by wangwei on 2018/5/5.
 */

public class BasicMathOp {

    public static class AddOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            Number sum = 0;

            for (Object param : params) {

                final Number value = asNum(param, galois);

                if (param instanceof Integer && sum instanceof Integer) {
                    sum = sum.intValue() + value.intValue();
                    continue;
                }

                sum = sum.doubleValue() + value.doubleValue();
            }

            return sum;
        }
    }

    public static class MinusOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            if (params.isEmpty()) {
                return 0;
            }

            if (params.size() == 1) {

                final Number value = asNum(params.get(0), galois);

                if (value instanceof Integer) {
                    return -1 * value.intValue();
                }

                return -1 * value.doubleValue();
            }

            final Number value = asNum(params.get(0), galois);

            Number result;
            if (value instanceof Integer) {
                result = value.intValue();
            } else {
                result = value.doubleValue();
            }

            for (int i = 1; i < params.size(); i++) {
                final Number num = asNum(params.get(i), galois);

                if (result instanceof Integer && num instanceof Integer) {
                    result = result.intValue() - num.intValue();
                    continue;
                }

                result = result.doubleValue() - num.doubleValue();
            }

            return result;
        }
    }

    public static class MultiOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            Number result = 1;
            for (Object param : params) {
                final Number num = asNum(param, galois);

                if (result instanceof Integer && num instanceof Integer) {
                    result = result.intValue() * num.intValue();
                    continue;
                }

                result = result.doubleValue() * num.doubleValue();
            }

            return result;
        }
    }

    public static class DivideOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 2, "/ operator need at least 2 args, but given: " + params);

            final Number num = asNum(params.get(0), galois);
            Number result = num instanceof Integer ? num.intValue() : num.doubleValue();

            for (int i = 1; i < params.size(); i++) {
                final Number value = asNum(params.get(i), galois);
                result = result.doubleValue() / value.doubleValue();
            }

            return result;
        }
    }

    public static class ModOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 2, "% operator need at least 2 args, but given: " + params);

            int remind = asInt(params.get(0), galois);

            for (int i = 1; i < params.size(); i++) {
                remind %= asInt(params.get(i), galois);
            }

            return remind;
        }
    }

    private static Number asNum(Object obj, Galois galois) {
        try {
            return ((Number) obj);
        } catch (Exception e) {
            galois.fatal("math op only accept number, bug given: " + obj, e);
            throw new AssertionError("impossible");
        }
    }

    private static Integer asInt(Object obj, Galois galois) {
        try {
            return ((Integer) obj);
        } catch (Exception e) {
            galois.fatal("mod op only accept int, bug given: " + obj, e);
            throw new AssertionError("impossible");
        }
    }
}
