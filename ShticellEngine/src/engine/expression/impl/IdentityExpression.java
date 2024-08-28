package engine.expression.impl;

import engine.expression.api.Expression;
import engine.cell.impl.CellType;
import engine.cell.api.EffectiveValue;
import engine.cell.impl.EffectiveValueImpl;

public class IdentityExpression implements Expression {

    private final Object value;
    private final CellType type;

    public IdentityExpression(Object value, CellType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(type, value);
    }

    @Override
    public CellType getFunctionResultType() {return type;}
}
