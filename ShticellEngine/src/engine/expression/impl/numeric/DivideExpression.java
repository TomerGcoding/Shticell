package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class DivideExpression implements Expression {
    private final Expression left;
    private final Expression right;
    private boolean divisible;
    public DivideExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
        divisible = true;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        // do some checking... error handling...
        try {
            double result = leftValue.extractValueWithExpectation(Double.class) / rightValue.extractValueWithExpectation(Double.class);
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }
        catch (ArithmeticException e)
        {
            divisible = false;
            return new EffectiveValueImpl(CellType.STRING, "NaN");
        }

    }
    @Override
    public CellType getFunctionResultType() {return divisible? CellType.NUMERIC: CellType.STRING; }

}
