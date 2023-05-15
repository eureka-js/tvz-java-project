package exceptions.unchecked;

import java.util.InputMismatchException;

public class WrongCoordInputFormatException extends InputMismatchException {
    public WrongCoordInputFormatException() {
    }

    public WrongCoordInputFormatException(String s) {
        super(s);
    }
}
