package xin.galois.lang.builtin;

import xin.galois.lang.Env;
import xin.galois.lang.Galois;
import xin.galois.lang.Functor;

import java.util.Collection;
import java.util.List;

/**
 * (=)
 * (==)
 * (>)
 * (<)
 * (>=)
 * (<=)
 * (=)
 * Created by wangwei on 2018/5/3.
 */

public class BasicBoolOp {

    private BasicBoolOp() {
        //no instance
    }

    public static boolean bool(Object obj) {
        if (obj instanceof Boolean) {
            return ((Boolean) obj);
        }

        if (obj instanceof Collection) {
            return !((List) obj).isEmpty();
        }

        if (obj instanceof CharSequence) {
            return !obj.toString().isEmpty();
        }

        if (obj instanceof Integer || obj instanceof Long) {
            return !obj.equals(0);
        }

        if (obj instanceof Double || obj instanceof Float) {
            return !obj.equals(0.0) && !obj.equals(-0.0);
        }

        return false;
    }

    public static class AndOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 1, "and operator need at least 1 arg, but given: " + params);

            for (Object param : params) {
                if (!bool(param)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class OrOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 1, "or operator need at least 1 arg, but given: " + params);

            for (Object param : params) {
                if (bool(param)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static class NotOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            galois.assertTrue(params.size() == 1, "not operator only accept one arg, but given: " + params);

            return !bool(params.get(0));
        }
    }

    public static abstract class CompareOp implements Functor {

        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 2, "compare operator need at least 2 args, but given: " + params);
            final Object left = params.get(0);
            final Object right = params.get(1);

            if (!compare(left, right)) {
                return false;
            }

            for (int i = 1; i < params.size() - 1; i++) {
                final Object _left = params.get(i);
                final Object _right = params.get(i + 1);
                if (!compare(_left, _right)) {
                    return false;
                }
            }

            return true;
        }

        protected abstract boolean compare(Object left, Object right);
    }

    /**
     * (> 3 4)
     */
    public static class GTOp extends CompareOp {
        @Override
        protected boolean compare(Object left, Object right) {
            return ((Comparable) left).compareTo(right) > 0;
        }
    }

    /**
     * (>= 3 4)
     */
    public static class GEOp extends CompareOp {
        @Override
        protected boolean compare(Object left, Object right) {
            return ((Comparable) left).compareTo(right) >= 0;
        }
    }

    public static class LTOp extends CompareOp {
        @Override
        protected boolean compare(Object left, Object right) {
            return ((Comparable) left).compareTo(right) < 0;
        }
    }

    public static class LEOp extends CompareOp {
        @Override
        protected boolean compare(Object left, Object right) {
            return ((Comparable) left).compareTo(right) <= 0;
        }
    }

    public static class EQOp extends CompareOp {

        @Override
        protected boolean compare(Object left, Object right) {
            return ((Comparable) left).compareTo(right) == 0;
        }
    }

    public static class EqualsOp extends CompareOp {

        @Override
        protected boolean compare(Object left, Object right) {
            return left.equals(right);
        }
    }

    public static class NoEqualsOp extends CompareOp {

        @Override
        protected boolean compare(Object left, Object right) {
            return !left.equals(right);
        }
    }

    public static class BoolOp implements Functor {

        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() == 1, "bool operator only accept 1 arg, but given: " + params);

            final Object obj = params.get(0);
            return bool(obj);
        }

    }

}
