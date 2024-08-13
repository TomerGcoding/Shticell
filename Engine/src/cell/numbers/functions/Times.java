package cell.numbers.functions;

import cell.numbers.NumCell;

public class Times implements BinaryNumFunction{
    public Double apply(NumCell cell1, NumCell cell2) {
        return cell1.getEffectiveValue() * cell2.getEffectiveValue(); }

}
