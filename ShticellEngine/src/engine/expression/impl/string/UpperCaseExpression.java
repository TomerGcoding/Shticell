package engine.expression.impl.string;
import engine.expression.api.Expression;
import engine.sheet.api.EffectiveValue;
import engine.sheet.api.CellType;
import engine.sheet.impl.EffectiveValueImpl;

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
