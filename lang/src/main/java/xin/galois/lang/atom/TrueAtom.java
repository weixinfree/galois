package xin.galois.lang.atom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by wangwei on 2018/5/8.
 */

public class TrueAtom extends AbsRegexAtom {

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("false\\b");
    }

    @Override
    protected Object handleRegexResult(String se, int index, Matcher matcher) {
        return Boolean.FALSE;
    }

    @Override
    public String toString() {
        return "true";
    }
}
