package exceptions.checked;

public class OutOfGameGridException extends Exception {
    public OutOfGameGridException() {
    }

    public OutOfGameGridException(String message) {
        super(message);
    }

    public OutOfGameGridException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutOfGameGridException(Throwable cause) {
        super(cause);
    }

    public OutOfGameGridException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
