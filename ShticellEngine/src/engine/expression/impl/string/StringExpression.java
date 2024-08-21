package engine.expression.impl.string;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class StringExpression implements Expression {
    private final String value;

    public StringExpression(String value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(CellType.STRING, value);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.STRING;
    }

    @Override
    public String toString() {return value;}
}
