package engine.expression.impl.string;
import engine.expression.api.Expression;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.api.CellType;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class UpperCaseExpression implements Expression {

    private final String value;

    public UpperCaseExpression(String value) {
        this.value = value;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(CellType.STRING, value.toUpperCase());
    }
}
