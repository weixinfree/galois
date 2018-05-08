package xin.galois.lang.atom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 浮点数
 * Created by wangwei on 2018/5/8.
 */
public class FloatAtom extends AbsRegexAtom {

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("[+-]?\\d+\\.\\d+\\b");
    }

    @Override
    protected Object handleRegexResult(String se, int index, Matcher matcher) {
        return Double.parseDouble(se.substring(matcher.start(), matcher.end()));
    }

    @Override
    public String toString() {
        return "FloatAtom";
    }
}
