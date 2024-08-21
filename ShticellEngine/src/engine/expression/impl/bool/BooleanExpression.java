package engine.expression.impl.bool;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class BooleanExpression implements Expression {

    private final String value;

    public BooleanExpression(String value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(CellType.BOOLEAN, value);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }
}
