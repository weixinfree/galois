package xin.galois.lang.atom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xin.galois.lang.Galois;

/**
 * None
 * <p>
 * Created by wangwei on 2018/5/8.
 */
public class NoneAtom extends AbsRegexAtom {

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("None\\b");
    }

    @Override
    protected Object handleRegexResult(String se, int index, Matcher matcher) {
        return Galois.None;
    }

    @Override
    public String toString() {
        return "NoneAtom";
    }
}
