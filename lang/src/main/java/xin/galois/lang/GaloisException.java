package xin.galois.lang;

/**
 * S表达式求值发生异常
 *
 * Created by wangwei on 2018/5/3.
 */
public class GaloisException extends RuntimeException {

    public GaloisException(String message) {
        super(message);
    }

    public GaloisException(String message, Throwable cause) {
        super(message, cause);
    }
}
