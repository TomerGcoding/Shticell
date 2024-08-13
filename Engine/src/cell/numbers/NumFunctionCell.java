package cell.numbers;

import cell.numbers.functions.BinaryNumFunction;
import cell.numbers.functions.Modulo;

public class NumFunctionCell extends NumCell {
    private BinaryNumFunction function;
    private NumCell cell1;
    private NumCell cell2;

    public NumFunctionCell(BinaryNumFunction function, NumCell cell1, NumCell cell2) {
        super(null);  // The value is computed, so we initialize it with null
        this.function = function;
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.value = function.apply(cell1, cell2);
    }

    @Override
    public Double getEffectiveValue() {
        return function.apply(cell1, cell2);
    }
}


