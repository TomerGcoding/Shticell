package engine.expression.api.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class ModuloExpression implements Expression {
    private Expression left;
    private Expression right;
    public ModuloExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        // do some checking... error handling...
        int result = leftValue.extractValueWithExpectation(Integer.class) % rightValue.extractValueWithExpectation(Integer.class);

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }
}
