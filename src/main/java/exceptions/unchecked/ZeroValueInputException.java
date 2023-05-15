package exceptions.unchecked;

import java.util.InputMismatchException;

public class ZeroValueInputException extends InputMismatchException {
    public ZeroValueInputException() {
    }

    public ZeroValueInputException(String s) {
        super(s);
    }
}
