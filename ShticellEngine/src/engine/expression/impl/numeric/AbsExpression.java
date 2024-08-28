package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.cell.impl.CellType;
import engine.cell.api.EffectiveValue;
import engine.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class AbsExpression extends NumericExpression {
    private final Expression expression;
    public AbsExpression(Expression expression) {
        this.expression = expression;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue value = expression.eval();
        try {
            double result = Math.abs(value.extractValueWithExpectation(Double.class));
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }
        catch (Exception e) {
            return new EffectiveValueImpl((CellType.UNKNOWN), "NaN" );
        }
    }

    @Override
    public List<Expression> getExpressions() {
        List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(expression);
        return expressions;
    }

}
