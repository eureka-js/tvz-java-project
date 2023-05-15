package exceptions.unchecked;

import java.util.InputMismatchException;

public class NegativeStatInputException extends RuntimeException {
    public NegativeStatInputException() {
    }

    public NegativeStatInputException(String s) {
        super(s);
    }
}
