package engine.expression.api.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class PowExpression implements Expression {
    private Expression left;
    private Expression right;
    public PowExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        // do some checking... error handling...
        double result = Math.pow(leftValue.extractValueWithExpectation(Double.class),rightValue.extractValueWithExpectation(Double.class));

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }
}
