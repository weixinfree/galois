package xin.galois.lang.builtin;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import xin.galois.lang.Env;
import xin.galois.lang.Functor;
import xin.galois.lang.Galois;
import xin.galois.lang.GaloisException;

/**
 * (new )
 * (class )
 * (. Integer parseInt '32.2')
 * (print)
 * (println)
 * <p>
 * Created by wangwei on 2018/5/5.
 */

public class BasicJavaInterpretiveOp {
    private static String formatPrintParams(List<Object> params) {
        final StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(String.valueOf(param)).append(" ");
        }
        sb.deleteCharAt(sb.lastIndexOf(" "));

        return sb.toString();
    }

    /**
     * (. store :dispatch :ACTION 3)
     */
    public static class DotOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 2, ". operator need at least 2 args, but given: " + params);

            final Object receiver = params.get(0);
            final String method = ((String) params.get(1));

            final Class<?> clazz = receiver instanceof Class ? ((Class) receiver) : receiver.getClass();

            if (params.size() > 2) {

                final Object[] args = new ArrayList<>(params).subList(2, params.size()).toArray();

                final Class[] argTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    argTypes[i] = args[i].getClass();
                }

                //1. direct call
                try {
                    final Method _method = clazz.getMethod(method, argTypes);
                    if (method != null) {
                        _method.setAccessible(true);
                        return _method.invoke(receiver, args);
                    }
                } catch (IllegalAccessException e) {
                    return galois.fatal("call method: " + method + " on object: " + receiver + " failed, because: ", e);
                } catch (InvocationTargetException e) {
                    return galois.fatal("call method: " + method + " on object: " + receiver + " failed, because: ", e);
                } catch (NoSuchMethodException e) {
                    // pass 2 next
                }

                //2. name and args match, (java primitive types as parameter situation)
                final Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (m.getName().equals(method) && m.getParameterTypes().length == args.length) {
                        try {
                            m.setAccessible(true);
                            return m.invoke(receiver, args);
                        } catch (IllegalAccessException e) {
                            return galois.fatal("call method: " + method + " on object: " + receiver + " failed, because: ", e);
                        } catch (InvocationTargetException e) {
                            return galois.fatal("call method: " + method + " on object: " + receiver + " failed, because: ", e);
                        }
                    }
                }

                // field set
                if (params.size() == 3) {
                    final String _field = method;

                    final Object value = params.get(2);
                    try {
                        final Field field = clazz.getField(_field);
                        if (field != null) {
                            field.setAccessible(true);
                            field.set(receiver, value);
                        }
                    } catch (NoSuchFieldException e) {
                        return galois.fatal("set field: " + _field + " on object: " + receiver + " failed, because: ", e);
                    } catch (IllegalAccessException e) {
                        return galois.fatal("set field: " + _field + " on object: " + receiver + " failed, because: ", e);
                    }
                }

                return galois.fatal("can not call method: " + method + " on object: " + receiver);
            }

            //1. unary method call
            try {
                final Method _method = clazz.getMethod(method);
                if (_method != null) {
                    _method.setAccessible(true);
                    return _method.invoke(receiver);
                }
            } catch (IllegalAccessException e) {
                return galois.fatal("call method: " + method + " on object: " + receiver + " failed, because: ", e);
            } catch (InvocationTargetException e) {
                return galois.fatal("call method: " + method + " on object: " + receiver + " failed, because: ", e);
            } catch (NoSuchMethodException e) {
                // pass, try fields
            }

            //2. field access
            try {
                final Field field = clazz.getField(method);
                if (field != null) {
                    field.setAccessible(true);
                    return field.get(receiver);
                }
            } catch (NoSuchFieldException e) {
                // pass try with map
            } catch (IllegalAccessException e) {
                return galois.fatal("access field: " + method + " on object: " + receiver + " failed, because: ", e);
            }

            return galois.fatal("can not call method/field: " + method + " on Object: " + receiver);
        }
    }

    public static class PrintlnOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            if (params.isEmpty()) {
                System.out.println();
                return Galois.None;
            }

            if (params.size() == 1) {
                System.out.println(params.get(0));
                return Galois.None;
            }

            System.out.println(formatPrintParams(params));
            return Galois.None;
        }
    }

    public static class PrintOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            if (params.isEmpty()) {
                return Galois.None;
            }

            if (params.size() == 1) {
                System.out.print(params.get(0));
                return Galois.None;
            }

            System.out.print(formatPrintParams(params));
            return Galois.None;
        }
    }

    public static class ClassOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() == 1, "class operator accept except 1 args, but given: " + params);

            final String clazzName = String.valueOf(params.get(0));
            try {
                return Class.forName(clazzName);
            } catch (ClassNotFoundException e) {
                throw new GaloisException("search class failed", e);
            }
        }
    }

    public static class NewOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {

            galois.assertTrue(params.size() >= 1, "new operator need at least 1 args, but given: " + params);

            final Class clazz = (Class) params.get(0);

            if (params.size() > 1) {
                final Object[] args = params.subList(1, params.size()).toArray(new Object[params.size() - 1]);

                final Class<?>[] argTypes = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    argTypes[i] = args[i].getClass();
                }

                try {
                    return clazz.getConstructor(argTypes).newInstance(args);
                } catch (InstantiationException e) {
                    throw new GaloisException("new operator failed because java call failed. class: " + clazz + ", params: " + params, e);
                } catch (IllegalAccessException e) {
                    throw new GaloisException("new operator failed because java call failed. class: " + clazz + ", params: " + params, e);
                } catch (InvocationTargetException e) {
                    throw new GaloisException("new operator failed because java call failed. class: " + clazz + ", params: " + params, e);
                } catch (NoSuchMethodException e) {
                    throw new GaloisException("new operator failed because java call failed. class: " + clazz + ", params: " + params, e);
                }
            }

            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new GaloisException("new operator failed because java call failed. class: " + clazz, e);
            } catch (IllegalAccessException e) {
                throw new GaloisException("new operator failed because java call failed. class: " + clazz, e);
            } catch (InvocationTargetException e) {
                throw new GaloisException("new operator failed because java call failed. class: " + clazz, e);
            } catch (NoSuchMethodException e) {
                throw new GaloisException("new operator failed because java call failed. class: " + clazz, e);
            }
        }
    }

    public static class AssertOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            galois.assertTrue(params.size() <= 2,
                    "assert operator take at most 2 args, but given: " + params);

            final boolean bool = BasicBoolOp.bool(params.get(0));

            if (!bool) {
                if (params.size() == 2) {
                    final String msg = String.valueOf(params.get(1));
                    throw new GaloisException(msg);
                } else {
                    throw new GaloisException("Assertion Error!");
                }
            }

            return Galois.None;
        }
    }
}
