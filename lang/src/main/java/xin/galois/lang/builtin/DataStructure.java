package xin.galois.lang.builtin;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;

import xin.galois.lang.Functor;
import xin.galois.lang.Galois;
import xin.galois.lang.Util;


public class DataStructure {

    public static void load(Galois galois) {
        galois.registerFunctor("list", new List());
        galois.registerFunctor("array", new Array());
        galois.registerFunctor("dict", new Dict());
        galois.registerFunctor("set", new Set());
        galois.registerFunctor("len", new Len());
    }

    public static class Set extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, java.util.List<Object> args, Galois galois) {
            return new LinkedHashSet<>(args);
        }
    }

    /**
     * (let users (list :xm :xh :mdh))
     * (.size users)
     * (.get users 1)
     * (.set users 2)
     */
    public static class List extends Functor.EvaluatedFunctor {

        @Override
        protected Object doCall(String name, java.util.List<Object> args, Galois galois) {
            return new LinkedList<>(args);
        }
    }

    public static class Array extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, java.util.List<Object> args, Galois galois) {
            return args.toArray(new Object[args.size()]);
        }
    }

    public static class Dict extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, java.util.List<Object> args, Galois galois) {
            final LinkedHashMap<Object, Object> dict = new LinkedHashMap<>();

            return dict;
        }
    }

    public static class Len extends Functor.EvaluatedFunctor {
        @Override
        protected Object doCall(String name, java.util.List<Object> args, Galois galois) {

            Util.assertTrue(args.size() == 1, "len functor accept exactly 1 arg, but given: " + args);

            final Object obj = args.get(0);

            if (obj.getClass().isArray()) {
                return java.lang.reflect.Array.getLength(obj);
            }

            if (obj instanceof String) {
                return ((String) obj).length();
            }

            if (obj instanceof Collection) {
                return ((Collection) obj).size();
            }

            if (obj instanceof Map) {
                return ((Map) obj).size();
            }

            if (obj instanceof Iterable) {
                final Iterator iterator = ((Iterable) obj).iterator();
                int len = 0;
                while (iterator.hasNext()) {
                    iterator.next();
                    len++;
                }

                return len;
            }

            return 1;
        }
    }
}
