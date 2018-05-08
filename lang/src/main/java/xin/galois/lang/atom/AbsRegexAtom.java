package xin.galois.lang.atom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则匹配原子
 * Created by wangwei on 2018/5/8.
 */

public abstract class AbsRegexAtom implements Atom {


    public AbsRegexAtom() {
    }

    protected abstract Pattern createPattern();

    protected abstract Object handleRegexResult(String se, int index, Matcher matcher);

    private Pattern pattern;

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = createPattern();
        }
        return pattern;
    }

    private int step;

    @Override
    public Object evalAtom(String se, int index) {
        
        final Matcher matcher = getPattern().matcher(se);
        if (matcher.find(index)) {
            if (matcher.start() == index) {
                this.step = matcher.end() - index;
                return handleRegexResult(se, index, matcher);
            }
        }

        return null;
    }

    @Override
    public int getStepForward() {
        return step;
    }
}
