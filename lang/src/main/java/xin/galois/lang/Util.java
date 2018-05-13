package xin.galois.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import xin.galois.lang.iternal.StringIterable;


public class Util {

    static Object reflectCall(Object receiver, String method, List<Object> args) {
        final Class clazz = receiver instanceof Class ? ((Class) receiver) : receiver.getClass();

        if (args.isEmpty()) {

            try {
                final Method m = clazz.getMethod(method);
                if (m != null) {
                    m.setAccessible(true);
                    return m.invoke(receiver);
                }
            } catch (NoSuchMethodException e) {
                // pass through
            } catch (IllegalAccessException e) {
                throw new EvalException("call Java Method: " + method + " on Object: " + receiver + " failed", e);
            } catch (InvocationTargetException e) {
                throw new EvalException("call Java Method: " + method + " on Object: " + receiver + " failed", e);
            }

            try {
                final Field field = clazz.getField(method);
                if (field != null) {
                    field.setAccessible(true);
                    return field.get(receiver);
                }
            } catch (NoSuchFieldException e) {
                // pass through
            } catch (IllegalAccessException e) {
                throw new EvalException("access Java Field " + method + " on Object: " + receiver + " failed", e);
            }


        } else {

            // exactly method find
            {
                try {
                    final Method m = clazz.getMethod(method, toTypeArgs(args));
                    if (m != null) {
                        m.setAccessible(true);
                        return m.invoke(receiver, args.toArray(new Object[args.size()]));
                    }
                } catch (NoSuchMethodException e) {
                    // pass through
                } catch (IllegalAccessException e) {
                    throw new EvalException("call Java Method: " + method + " on Object: " + receiver + " failed", e);
                } catch (InvocationTargetException e) {
                    throw new EvalException("call Java Method: " + method + " on Object: " + receiver + " failed", e);
                }
            }
            // name method find
            {
                final Method[] methods = clazz.getMethods();
                for (Method m : methods) {
                    if (m.getName().equals(method) && isParameterAndArgsMatch(m.getParameterTypes(), args)) {
                        try {
                            return m.invoke(receiver, args.toArray(new Object[args.size()]));
                        } catch (IllegalAccessException e) {
                            throw new EvalException("call Java Method: " + method + " on Object: " + receiver + " failed", e);
                        } catch (InvocationTargetException e) {
                            throw new EvalException("call Java Method: " + method + " on Object: " + receiver + " failed", e);
                        }
                    }
                }
            }

            // field set
            if (args.size() == 1) {
                final Object value = args.get(0);
                try {
                    final Field field = clazz.getField(method);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(receiver, value);
                        return value;
                    }
                } catch (NoSuchFieldException e) {
                    throw new EvalException("access Java Field " + method + " on Object: " + receiver + " failed", e);
                } catch (IllegalAccessException e) {
                    throw new EvalException("access Java Field " + method + " on Object: " + receiver + " failed", e);
                }
            }
        }

        throw new EvalException("object: " + receiver + "has no method or field match name: " + method);

    }

    // TODO-wei: 2018/5/12 异常处理
    static Object newObj(Class clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new EvalException("new Object failed", e);
        } catch (IllegalAccessException e) {
            throw new EvalException("new Object failed", e);
        } catch (InvocationTargetException e) {
            throw new EvalException("new Object failed", e);
        } catch (NoSuchMethodException e) {
            throw new EvalException("new Object failed", e);
        }
    }

    // TODO-wei: 2018/5/12 异常处理
    static Object newObj(Class clazz, List<Object> args) {

        try {
            final Constructor constructor = clazz.getConstructor(toTypeArgs(args));
            if (constructor != null) {
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            }
        } catch (InstantiationException e) {
            throw new EvalException("new Object failed", e);
        } catch (IllegalAccessException e) {
            throw new EvalException("new Object failed", e);
        } catch (InvocationTargetException e) {
            throw new EvalException("new Object failed", e);
        } catch (NoSuchMethodException e) {
            // pass through
        }

        final Constructor[] constructors = clazz.getConstructors();
        for (Constructor c : constructors) {
            if (isParameterAndArgsMatch(c.getParameterTypes(), args)) {
                try {
                    return c.newInstance(args);
                } catch (InstantiationException e) {
                    throw new EvalException("new Object failed", e);
                } catch (IllegalAccessException e) {
                    throw new EvalException("new Object failed", e);
                } catch (InvocationTargetException e) {
                    throw new EvalException("new Object failed", e);
                }
            }
        }

        throw new EvalException("new Object failed: no proper constructor find");
    }

    private static boolean isParameterAndArgsMatch(Class<?>[] parameterTypes, List<Object> args) {
        if (parameterTypes.length != args.size()) {
            return false;
        }

        for (int i = 0; i < parameterTypes.length; i++) {
            final Class<?> type = parameterTypes[i];
            final Object arg = args.get(i);

            // 基础数据类型不接受null
            if (type.isPrimitive() && arg == null) {
                return false;
            }

            // null 适配于所有引用类型
            if (!type.isPrimitive() && arg == null) {
                continue;
            }

            // 引用类型，必须是instanceOf 关系
            if (!type.isPrimitive() && !type.isAssignableFrom(arg.getClass())) {
                return false;
            }

            if (type.isPrimitive()) {

                if (type == boolean.class && !isAssignableToPrimitiveBool(arg)) {
                    return false;
                }

                if (type == char.class && !isAssignableToPrimitiveChar(arg)) {
                    return false;
                }

                if (type == byte.class && !isAssignableToPrimitiveByte(arg)) {
                    return false;
                }

                if (type == short.class && !isAssignableToPrimitiveShort(arg)) {
                    return false;
                }

                if (type == int.class && !isAssignableToPrimitiveInt(arg)) {
                    return false;
                }

                if (type == long.class && !isAssignableToPrimitiveLong(arg)) {
                    return false;
                }

                if (type == float.class && !isAssignableToPrimitiveFloat(arg)) {
                    return false;
                }

                if (type == double.class && !isAssignableToPrimitiveDouble(arg)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isAssignableToPrimitiveByte(Object arg) {
        return arg instanceof Byte;
    }

    private static boolean isAssignableToPrimitiveShort(Object arg) {
        return isAssignableToPrimitiveByte(arg) || arg instanceof Short;
    }

    private static boolean isAssignableToPrimitiveInt(Object arg) {
        return isAssignableToPrimitiveShort(arg) || arg instanceof Integer;
    }

    private static boolean isAssignableToPrimitiveLong(Object arg) {
        return isAssignableToPrimitiveInt(arg) || arg instanceof Long;
    }

    private static boolean isAssignableToPrimitiveFloat(Object arg) {
        return isAssignableToPrimitiveLong(arg) || arg instanceof Float;
    }

    private static boolean isAssignableToPrimitiveDouble(Object arg) {
        return isAssignableToPrimitiveFloat(arg) || arg instanceof Double;
    }

    private static boolean isAssignableToPrimitiveChar(Object arg) {
        return arg instanceof Character || arg instanceof Byte;
    }

    private static boolean isAssignableToPrimitiveBool(Object arg) {
        return arg instanceof Boolean;
    }

    private static Class[] toTypeArgs(List<Object> args) {
        final List<Class> argTypes = new ArrayList<>(args.size());
        for (Object arg : args) {
            argTypes.add(arg.getClass());
        }

        return argTypes.toArray(new Class[argTypes.size()]);
    }

    public static boolean bool(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof String) {
            return !((String) obj).isEmpty();
        }

        if (obj instanceof Collection) {
            return !((Collection) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return !((Map) obj).isEmpty();
        }

        if (obj instanceof Integer) {
            return !equals(obj, 0);
        }

        if (obj instanceof Double) {
            return !equals(obj, 0.0);
        }

        return true;
    }

    public static Iterable asIterable(Object object) {
        if (object instanceof Iterable) {
            return ((Collection) object);
        }

        if (object.getClass().isArray()) {
            return Arrays.asList(((Object[]) object));
        }

        if (object instanceof String) {
            return new StringIterable(((String) object));
        }

        throw new EvalException("not a iterable type: " + object);
    }

    public static String template(String template, EvalEnv env) {

        final StringBuilder result = new StringBuilder(template.length());

        int openIndex = -2;
        int closeIndex = -1;

        while (openIndex < template.length()) {

            final int start = closeIndex + 1;

            openIndex = template.indexOf("${", closeIndex + 1);
            if (openIndex == -1) break;

            closeIndex = template.indexOf("}", openIndex + 2);
            if (closeIndex == -1) break;

            result.append(template, start, openIndex);
            final String key = template.substring(openIndex + 2, closeIndex).trim();
            result.append(env.get(key));
        }

        if (closeIndex + 1 < template.length()) {
            result.append(template, closeIndex + 1, template.length());
        }

        return result.toString();
    }

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static void assertTrue(boolean expr, String msg) {
        if (!expr) {
            throw new EvalException(msg);
        }
    }

    public static Number asNum(Object obj) {
        try {
            return ((Number) obj);
        } catch (ClassCastException e) {
            throw new EvalException("except as Number, but get: " + obj.getClass(), e);
        }
    }

    public static Integer asInt(Object obj) {
        try {
            return ((Integer) obj);
        } catch (ClassCastException e) {
            throw new EvalException("except as Integer, but get: " + obj.getClass(), e);
        }
    }

    public static Class asClass(Object obj) {
        try {
            return ((Class) obj);
        } catch (ClassCastException e) {
            throw new EvalException("except as Class, but get: " + obj.getClass());
        }
    }
}
