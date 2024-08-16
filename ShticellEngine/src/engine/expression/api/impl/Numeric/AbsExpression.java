package engine.expression.api.impl.Numeric;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class AbsExpression implements Expression {
    private Expression expression;
    public AbsExpression(Expression expression) {
        this.expression = expression;
    }
    public EffectiveValue eval() {
        EffectiveValue value = expression.eval();
        // do some checking... error handling...
        double result = Math.abs(value.extractValueWithExpectation(Double.class));

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }
}
