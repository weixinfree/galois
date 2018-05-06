package xin.galois.lang;

import java.util.List;

public interface Functor {
    Object call(String operator, List<Object> params, Env env, Galois galois);
}
