package Exceptions;

public class CustomDivideByZeroException extends RuntimeException {
    public CustomDivideByZeroException(String message) {
        super(message);
    }
}
