package xin.galois.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 函数
 * Created by wangwei on 2018/5/4.
 */

public class Fn implements Functor {

    private static final Pattern PARAM = Pattern.compile("(\\w+)");

    private final String name;
    private final List<String> params;
    private final String body;
    private final Galois envSnapshotGalois;

    Fn(String name, String params, String body, Galois galois) {
        this.name = name;
        this.params = parseParameters(params);
        this.body = body;
        this.envSnapshotGalois = new Galois(galois);
    }

    private static List<String> parseParameters(String params) {
        final ArrayList<String> parameters = new ArrayList<>();

        final Matcher matcher = PARAM.matcher(params);
        int start = 0;
        while (matcher.find(start)) {
            parameters.add(matcher.group(1).trim());
            start = matcher.end();
        }

        return parameters;
    }

    @Override
    public Object call(String operator, List<Object> args, Env $_, Galois $) {

        final Galois galois = this.envSnapshotGalois;

        final Env _env = galois.env;
        _env.pushNewEnv();

        final List<String> formalParams = this.params;
        galois.assertTrue(args.size() == formalParams.size(),
                "formal parameters count mismatch arguments count, params: " + formalParams + ", args: " + args);

        _env.set("$*", args);

        for (int i = 0; i < formalParams.size(); i++) {
            _env.set(formalParams.get(i), args.get(i));
        }

        try {
            return galois.eval(body);
        } finally {
            _env.popEnv();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(fn ");
        sb.append(name);
        sb.append("(");
        for (String param : this.params) {
            sb.append(param).append(" ");
        }
        sb.deleteCharAt(sb.lastIndexOf(" "));
        sb.append(body);
        sb.append(")");

        return sb.toString();
    }
}
