package xin.galois.lang;

/**
 * Galois 相关异常
 * Created by wangwei on 2018/5/11.
 */

@SuppressWarnings("WeakerAccess")
public class GaloisException extends RuntimeException {

    public GaloisException(String message) {
        super(message);
    }

    public GaloisException(String message, Throwable cause) {
        super(message, cause);
    }
}
