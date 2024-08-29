package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.cell.impl.CellType;
import engine.cell.api.EffectiveValue;
import engine.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class PowExpression extends NumericExpression {
    private final Expression left;
    private final Expression right;
    public PowExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        try {
            double base = leftValue.extractValueWithExpectation(Double.class);
            double pow = rightValue.extractValueWithExpectation(Double.class);
            if (base == 0 && pow == 0)
                return new EffectiveValueImpl((CellType.UNKNOWN), "NaN" );

            double result = Math.pow(base, pow);
            return new EffectiveValueImpl(CellType.NUMERIC, result);

        }
        catch (Exception e) {
            return new EffectiveValueImpl((CellType.UNKNOWN), "NaN" );
        }

    }

    @Override
    public List<Expression> getExpressions() {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(left);
        expressions.add(right);
        return expressions;
    }
}
