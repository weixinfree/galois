package xin.galois.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Java的互解释性
 * Created by wangwei on 2018/5/12.
 */

@SuppressWarnings("WeakerAccess")
public class JavaInterop {

    private static final Pattern DOT_OP = Pattern.compile("\\.(\\w+)");

    public static Object interop(String operator, java.util.List<AstNode> params, Galois galois) {
        final Matcher matcher = DOT_OP.matcher(operator);
        if (!matcher.matches()) {
            return null;
        }

        final Object receiver = galois.eval(params.get(0));

        final String name = matcher.group(1);

        if ("new".equals(name)) {

            if (params.size() >= 2) {
                final List<AstNode> _args = params.subList(1, params.size());
                final List<Object> args = new ArrayList<>(_args.size());
                // TODO-wei: 2018/5/13

            } else if (params.size() == 1){

            } else {
                throw new EvalException("Wrong New Object call: <(.new Object)>");
            }
        }



        return null;

    }
}
