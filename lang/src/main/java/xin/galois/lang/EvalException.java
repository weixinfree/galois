package xin.galois.lang;

/**
 *
 * Created by wangwei on 2018/5/11.
 */

public class EvalException extends GaloisException {

    public EvalException(String message) {
        super(message);
    }

    public EvalException(String message, Throwable cause) {
        super(message, cause);
    }
}
