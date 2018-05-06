package xin.galois.lang.builtin;

import xin.galois.lang.Env;
import xin.galois.lang.Galois;
import xin.galois.lang.Functor;
import xin.galois.lang.Record;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * (int)
 * (str)
 * (array)
 * (set)
 * (list)
 * (len)
 * todo:
 * (dict)
 * Created by wangwei on 2018/5/5.
 */

public class BasicDataTypesOp {

    public static class StrOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            final StringBuilder result = new StringBuilder();
            for (Object param : params) {
                result.append(String.valueOf(param));
            }

            return result.toString();
        }
    }

    /**
     * (int 3.2)
     * (int '32')
     * (int '0x32' 16)
     * (int '0o32' 8)
     */
    public static class IntOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 1, "int operator need at least 1 arg, but given: " + params);

            final Object num = params.get(0);

            if (num instanceof String) {

                if (params.size() > 1) {
                    galois.assertTrue(params.size() == 2, "(int str radix) mode accept 2 args, but given: " + params);

                    final Integer radix = (Integer) params.get(1);
                    return Integer.parseInt(((String) num), radix);
                }

                return Integer.parseInt(((String) num));
            }

            if (num instanceof Number) {

                galois.assertTrue(params.size() == 1, "(int num) mode accept only 1 arg, but given: " + params);

                return ((int) ((Number) num).doubleValue());
            }

            return galois.fatal("wrong args for int method: " + params);
        }
    }

    public static class ArrayOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            return params.toArray(new Object[params.size()]);
        }
    }

    public static class ListOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            return new ArrayList<>(params);
        }
    }

    public static class SetOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            return new LinkedHashSet<>(params);
        }
    }

    public static class LenOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 1, "len operator accept exactly 1 arg, but given: " + params);

            final Object obj = params.get(0);
            if (obj.getClass().isArray()) {
                return Array.getLength(obj);
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

            return galois.fatal("unsupported type for len operator: " + obj.getClass());
        }
    }

    public static class DictOp implements Functor {

        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            final Dict dict = new Dict();
            galois.assertTrue(params.size() % 2 == 0,
                    "dict operator need zero or even count params, now given: " + params);

            for (int i = 0; i < params.size(); i += 2) {
                final Object key = params.get(i);
                final Object value = params.get(i + 1);
                dict.put(key, value);
            }

            return dict;
        }
    }

    /**
     * (let user (dict :name 'xiaoming' :age 10 :sex 'male'))
     * (user :name)
     * (user :age)
     * (user :age 10)
     */
    static class Dict extends HashMap<Object, Object> implements Functor {

        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            if (params.size() == 1) {
                final Object key = params.get(0);
                if (containsKey(key)) {
                    return get(key);
                }

                return Galois.None;
            }

            if (params.size() == 2) {
                final Object key = params.get(0);
                final Object value = params.get(1);
                return put(key, value);
            }

            return galois.fatal("dict data type take at most 2 args, but given: " + params);
        }
    }

    public static class KeysOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            galois.assertTrue(params.size() == 1,
                    "keys operator accept exactly 1 arg, but given: " + params);

            final Object value = params.get(0);

            if (value instanceof Map) {
                return ((Map) value).keySet();
            }

            if (value instanceof Record.RecordInstance) {
                return ((Record.RecordInstance) value).__dict__.keySet();
            }

            return galois.fatal("keys op unsupported error! object: " + value);
        }
    }

    public static class ValuesOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() == 1,
                    "values operator accept exactly 1 arg, but given: " + params);

            final Object value = params.get(0);

            if (value instanceof Map) {
                return ((Map) value).values();
            }

            if (value instanceof Record.RecordInstance) {
                return ((Record.RecordInstance) value).__dict__.values();
            }

            return galois.fatal("values op unsupported error! object: " + value);
        }
    }

}
