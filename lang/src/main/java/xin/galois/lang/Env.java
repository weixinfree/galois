package xin.galois.lang;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
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

public class Env {

    private Deque<Map<String, Object>> envStack = new ArrayDeque<>();

    private Env(boolean init) {
        if (init) {
            pushNewEnv();
            initialBuiltInEnv();
        }
    }

    Env() {
        this(true);
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

    public Map<String, Object> globalEnv() {
        return envStack.getFirst();
    }

    public void setGlobal(String key, Object value) {
        globalEnv().put(key, value);
    }

    public void pushNewEnv() {
        envStack.push(new LinkedHashMap<String, Object>());
    }

    public void popEnv() {
        envStack.pop();
    }

    public void set(String key, Object value) {
        envStack.peek().put(key, value);
    }

    public Object get(String key) {
        for (Map<String, Object> _env : envStack) {
            if (_env.containsKey(key)) {
                return _env.get(key);
            }
        }

        throw new GaloisException("reference undefined variable: " + key);
    }

    public Env snapshot() {
        final Env snapshot = new Env(false);
        // 取当前环境的快照
        snapshot.envStack = new ArrayDeque<>(this.envStack);
        return snapshot;
    }

    public boolean contains(String var) {
        try {
            get(var);
            return true;
        } catch (GaloisException e) {
            return false;
        }
    }

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
