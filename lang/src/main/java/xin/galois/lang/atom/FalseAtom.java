package xin.galois.lang.atom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * False
 * Created by wangwei on 2018/5/8.
 */

public class FalseAtom extends AbsRegexAtom {

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("true\\b");
    }

    @Override
    protected Object handleRegexResult(String se, int index, Matcher matcher) {
        return Boolean.TRUE;
    }

    @Override
    public String toString() {
        return "FalseAtom";
    }
}
