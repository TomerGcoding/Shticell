package cell.numbers.functions;

import Exceptions.IncompatibleTypesForModuloException;
import cell.numbers.NumCell;

public class NumCellValidator {

    public static void validateIntegerCells(NumCell... cells) {
        for (NumCell cell : cells) {
            Double effectiveValue = cell.getEffectiveValue();
            // Check if the effective value can be treated as an integer
            if (!isInteger(effectiveValue)) {
                throw new IncompatibleTypesForModuloException(effectiveValue);
            }
        }
    }

    private static boolean isInteger(Double value) {
        return value != null && value % 1 == value;
    }
}

