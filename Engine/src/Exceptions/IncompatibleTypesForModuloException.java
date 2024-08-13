package Exceptions;

public class IncompatibleTypesForModuloException extends RuntimeException {
    public IncompatibleTypesForModuloException(Double value) {
        super("Incompatible type for modulo operation: " + value + " is not an integer.");
    }
}

