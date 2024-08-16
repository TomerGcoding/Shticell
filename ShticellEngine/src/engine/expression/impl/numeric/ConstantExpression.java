package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

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
