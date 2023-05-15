package exceptions.unchecked;

public class DBObjectNotPresentException extends RuntimeException {
    public DBObjectNotPresentException() {
    }

    public DBObjectNotPresentException(String message) {
        super(message);
    }

    public DBObjectNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBObjectNotPresentException(Throwable cause) {
        super(cause);
    }

    public DBObjectNotPresentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
