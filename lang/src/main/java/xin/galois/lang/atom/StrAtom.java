package xin.galois.lang.atom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * str
 * <p>
 * 'hello world \'hello\'' = "hello world 'hello'"
 * Created by wangwei on 2018/5/8.
 */
public class StrAtom extends AbsRegexAtom {

    @Override
    protected Pattern createPattern() {
        return Pattern.compile("'(.*?)'");
    }

    @Override
    protected Object handleRegexResult(String se, int index, Matcher matcher) {
        return matcher.group(1);
    }

    @Override
    public String toString() {
        return "StrAtom";
    }
}
