package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class ConstantExpression implements Expression {
    private final double value;

    public ConstantExpression(double value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(CellType.NUMERIC, value);
    }
}
