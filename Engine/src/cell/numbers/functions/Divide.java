package cell.numbers.functions;

import Exceptions.CustomDivideByZeroException;
import cell.numbers.NumCell;

public class Divide implements BinaryNumFunction {
    public Double apply(NumCell cell1, NumCell cell2) {
        try {
            // Perform the division
            return val1 / val2;
        } catch (ArithmeticException e) {
            // Catch the built-in divide by zero exception and throw your custom exception
            throw new CustomDivideByZeroException("Division by zero error occurred. Please try again.");
        }
    }
}
