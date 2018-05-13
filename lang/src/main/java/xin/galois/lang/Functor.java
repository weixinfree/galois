package xin.galois.lang;

import java.util.ArrayList;
import java.util.List;

public interface Functor {

    Object call(String name, List<AstNode> params, Galois galois);

    abstract class EvaluatedFunctor implements Functor {

        @Override
        public final Object call(String name, List<AstNode> params, Galois galois) {
            final List<Object> args = new ArrayList<>(params.size());
            for (AstNode node : params) {
                args.add(galois.eval(node));
            }

            return doCall(name, args, galois);
        }

        protected abstract Object doCall(String name, List<Object> args, Galois galois);
    }
}
