package xin.galois.lang.atom;

/**
 *
 * Created by wangwei on 2018/5/6.
 */
public interface Atom {

    Object evalAtom(String se, int index);

    int getStepForward();
}


