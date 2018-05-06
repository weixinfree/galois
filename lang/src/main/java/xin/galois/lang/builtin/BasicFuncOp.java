package xin.galois.lang.builtin;

import xin.galois.lang.Env;
import xin.galois.lang.Galois;
import xin.galois.lang.Functor;

import java.util.List;

/**
 * todo:
 * (map)
 * (filter)
 * (reduce)
 * (zip)
 * (max)
 * (min)
 * (sorted)
 *
 * Created by wangwei on 2018/5/5.
 */

public class BasicFuncOp {

    public static class MapOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            return null;
        }
    }

    public static class FilterOp implements Functor {
        @Override
        public Object call(String operator, List<Object> params, Env env, Galois galois) {
            return null;
        }
    }
}
