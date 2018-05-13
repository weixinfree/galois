package xin.galois.lang;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 求值环境
 * Created by wangwei on 2018/5/11.
 */
@SuppressWarnings("WeakerAccess")
public class EvalEnv {

    private final Deque<Map<String, Object>> envStack;

    public EvalEnv(Deque<Map<String, Object>> envStack) {
        this.envStack = envStack;
    }

    public EvalEnv() {
        envStack = new LinkedList<>();
        // builtin Env
        pushEnv();
        initialBuiltInEnv();
        // global env
        pushEnv();
    }

    private void initialBuiltInEnv() {
        final List<Class<?>> builtInClasses = Arrays.asList(
                Byte.class,
                Short.class,
                Integer.class,
                Long.class,
                Float.class,
                Double.class,
                Character.class,
                Number.class,

                String.class,
                StringBuilder.class,
                CharSequence.class,

                Object.class,
                Class.class,
                Runnable.class,
                Callable.class,

                WeakReference.class,
                SoftReference.class,

                Constructor.class,
                Method.class,
                Field.class,

                System.class,
                Math.class,
                Thread.class,
                Runtime.class,
                Process.class,
                Void.class,

                Iterable.class,
                Iterator.class,
                Collection.class,
                List.class,
                Set.class,
                Map.class,
                Deque.class,
                Arrays.class,
                Queue.class,
                WeakHashMap.class,
                BitSet.class,

                Date.class,
                Calendar.class,

                Pattern.class,
                Matcher.class,
                MatchResult.class,

                TimeUnit.class,
                AtomicInteger.class
        );

        for (Class<?> clazz : builtInClasses) {
            set(clazz.getSimpleName(), clazz);
        }
    }

    @SuppressWarnings("unused")
    public Map<String, Object> globalEnv() {
        return envStack.getFirst();
    }

    public void let(String var, Object value) {
        envStack.peek().put(var, value);
    }

    public void set(String var, Object value) {
        for (Map<String, Object> map : envStack) {
            if (map.containsKey(var)) {
                map.put(var, value);
                return;
            }
        }

        let(var, value);
    }

    public Object get(String var) {
        for (Map<String, Object> map : envStack) {
            if (map.containsKey(var)) {
                return map.get(var);
            }
        }

        return Galois.UnDefined;
    }

    @SuppressWarnings("unused")
    public Object containsVar(String var) {
        for (Map<String, Object> map : envStack) {
            if (map.containsKey(var)) {
                return true;
            }
        }

        return false;
    }

    public EvalEnv snapshot() {
        return new EvalEnv(this.envStack);
    }

    public void pushEnv() {
        envStack.push(new HashMap<String, Object>());
    }

    public void popEnv() {
        envStack.pop();
    }

    @SuppressWarnings("unused")
    public void dump() {
        int level = 0;
        for (Map<String, Object> env : envStack) {
            for (Map.Entry<String, Object> entry : env.entrySet()) {
                System.out.println(pad(level) + " " + entry.getKey() + " = " + entry.getValue());
            }
            level += 1;
        }
    }

    private static String pad(int count) {
        if (count <= 0) {
            return "";
        }

        if (count == 1) {
            return "    ";
        }

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("    ");
        }

        return sb.toString();
    }
}
