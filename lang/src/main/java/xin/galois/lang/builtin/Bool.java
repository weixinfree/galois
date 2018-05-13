package xin.galois.lang.builtin;

import java.util.List;

import xin.galois.lang.EvalException;
import xin.galois.lang.Functor;
import xin.galois.lang.Galois;
import xin.galois.lang.Util;

import static xin.galois.lang.Util.assertTrue;
import static xin.galois.lang.Util.bool;

public class Bool {

    public static void load(Galois galois) {
        galois.registerFunctor("and", new And());
        galois.registerFunctor("or", new Or());
        galois.registerFunctor("not", new Not());
        galois.registerFunctor("gt", new GT());
        galois.registerFunctor("ge", new GE());
        galois.registerFunctor("eq", new EQ());
        galois.registerFunctor("le", new LE());
        galois.registerFunctor("lt", new LT());
        galois.registerFunctor("equals", new Equals());
        galois.registerFunctor("bool", new BoolF());
    }

    public static class And extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            assertTrue(params.size() >= 1, "and operator need at least 1 arg, but given: " + params);

            for (Object param : params) {
                if (!bool(param)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class Or extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            assertTrue(params.size() >= 1, "or operator need at least 1 arg, but given: " + params);

            for (Object param : params) {
                if (bool(param)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static class Not extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            assertTrue(params.size() == 1, "not operator only accept one arg, but given: " + params);

            return !bool(params.get(0));
        }
    }

    public static abstract class CompareFunctor extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            assertTrue(params.size() >= 2, "compare operator need at least 2 args, but given: " + params);
            final Object left = params.get(0);
            final Object right = params.get(1);

            if (!_compare(left, right)) {
                return false;
            }

            for (int i = 1; i < params.size() - 1; i++) {
                final Object _left = params.get(i);
                final Object _right = params.get(i + 1);
                if (!_compare(_left, _right)) {
                    return false;
                }
            }

            return true;
        }

        private boolean _compare(Object l, Object r) {
            if (l instanceof Number && r instanceof Number) {
                return compareNumber(((Number) l), ((Number) r));
            }

            if (l instanceof Comparable && r instanceof Comparable) {
                try {
                    return compare(((Comparable) l), ((Comparable) r));
                } catch (Exception e) {
                    throw new EvalException("compare error", e);
                }
            }

            throw new EvalException("unsupported comparable types:" + l + " vs " + r);
        }

        protected abstract boolean compareNumber(Number left, Number right);

        protected abstract boolean compare(Comparable left, Comparable right);
    }

    public static class GT extends CompareFunctor {

        @Override
        protected boolean compareNumber(Number left, Number right) {
            return left.doubleValue() > right.doubleValue();
        }

        @Override
        protected boolean compare(Comparable left, Comparable right) {
            return left.compareTo(right) > 0;
        }
    }

    public static class GE extends CompareFunctor {

        @Override
        protected boolean compareNumber(Number left, Number right) {
            return left.doubleValue() >= right.doubleValue();
        }

        @Override
        protected boolean compare(Comparable left, Comparable right) {
            return left.compareTo(right) >= 0;
        }

    }

    public static class EQ extends CompareFunctor {

        @Override
        protected boolean compareNumber(Number left, Number right) {
            return left.doubleValue() == right.doubleValue();
        }

        @Override
        protected boolean compare(Comparable left, Comparable right) {
            return left.compareTo(right) == 0;
        }
    }

    public static class LE extends CompareFunctor {

        @Override
        protected boolean compareNumber(Number left, Number right) {
            return left.doubleValue() <= right.doubleValue();
        }

        @Override
        protected boolean compare(Comparable left, Comparable right) {
            return left.compareTo(right) <= 0;
        }

    }

    public static class LT extends CompareFunctor {

        @Override
        protected boolean compareNumber(Number left, Number right) {
            return left.doubleValue() < right.doubleValue();
        }

        @Override
        protected boolean compare(Comparable left, Comparable right) {
            return left.compareTo(right) < 0;
        }
    }

    public static class Equals extends Functor.EvaluatedFunctor {

        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {
            assertTrue(params.size() >= 2, "== op need at least 2 args, bug given: " + params);

            final Object left = params.get(0);
            final Object right = params.get(1);

            if (!Util.equals(left, right)) {
                return false;
            }

            for (int i = 1; i < params.size() - 1; i++) {
                final Object _left = params.get(i);
                final Object _right = params.get(i + 1);
                if (!Util.equals(_left, _right)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static class BoolF extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, List<Object> params, Galois galois) {

            assertTrue(params.size() == 1, "bool operator only accept 1 arg, but given: " + params);

            final Object obj = params.get(0);
            return bool(obj);
        }
    }
}
