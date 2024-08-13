package cell.numbers.functions;

import cell.numbers.NumCell;

public class Modulo implements BinaryNumFunction {
    @Override
    public Double apply(NumCell cell1, NumCell cell2) {
        int val1 = cell1.getEffectiveValue().intValue();
        Double val2 = cell2.getEffectiveValue().intValue();

        // Check if the first number is an integer
        if (!cell1.isInt()) {
            throw new NonIntegerModuloException(val1);
        }

        return val1 % val2;
    }
}
