package xin.galois.lang.builtin;

import xin.galois.lang.Env;
import xin.galois.lang.Galois;
import xin.galois.lang.Functor;

import java.util.List;

/**
 * 基本的数学运算
 *
 * (+ 1 2)
 * (- 1 2)
 * (* 1 2)
 * (/ 1 2)
 * (% 1 2)
 *
 * todo
 *
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
            double sum = 0;
            for (Object param : params) {
                sum += ((Number) param).doubleValue();
            }

            return sum;
        }
    }

    public static class MinusOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            double sum = 0;

            if (params.size() == 1) {
                return -((Number) params.get(0)).doubleValue();
            }

            if (params.size() > 0) {
                sum = ((Number) params.get(0)).doubleValue();
            }

            for (int i = 1; i < params.size(); i++) {
                sum -= ((Number) params.get(i)).doubleValue();
            }

            return sum;
        }
    }

    public static class MultiOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            double result = 1;
            for (Object param : params) {
                result *= ((Number) param).doubleValue();
            }

            return result;
        }
    }

    public static class DivideOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 2, "/ operator need at least 2 args, but given: " + params);

            double result = ((Number) params.get(0)).doubleValue();

            for (int i = 1; i < params.size(); i++) {
                result /= ((Number) params.get(i)).doubleValue();
            }

            return result;
        }
    }

    public static class ModOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 2, "% operator need at least 2 args, but given: " + params);

            int remind = (Integer) params.get(0);

            for (int i = 1; i < params.size(); i++) {
                remind %= (Integer) params.get(i);
            }

            return remind;
        }
    }
}
