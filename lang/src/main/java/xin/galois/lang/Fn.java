package xin.galois.lang;

import android.annotation.SuppressLint;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Fn implements Functor {

    static Fn newNamedFn(String name, List<String> args, AstNode node, Galois galois) {
        return new Fn(name, args, node, galois.snapshot());
    }

    static Fn newAnonymousFn(List<String> args, AstNode block, Galois galois) {
        return new Fn(genAnonymousFnName(), args, block, galois.snapshot());
    }

    private static final AtomicInteger sAnonymousFnNameGenerator = new AtomicInteger(0);

    private static String genAnonymousFnName() {
        return "Anonymous@" + sAnonymousFnNameGenerator.getAndIncrement();
    }

    public final String name;
    @SuppressWarnings("WeakerAccess")
    public final List<String> params;
    private final AstNode block;
    private final Galois eval;

    private Fn(String name, List<String> params, AstNode block, Galois eval) {
        this.name = name;
        this.params = params;
        this.block = block;
        this.eval = eval;
    }

    String signature() {
        return name;
    }

    @SuppressLint("Assert")
    @Override
    public Object call(String name, List<AstNode> args, Galois galois) {
        eval.env.pushEnv();

        assert args.size() == this.params.size();

        try {
            for (int i = 0; i < args.size(); i++) {
                eval.env.set(params.get(i), args.get(i));
            }

            eval.env.set("$*", args);
            return eval.eval(block);
        } finally {
            eval.env.popEnv();
        }
    }
}
