package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class TimesExpression extends NumericExpression {
    private final Expression left;
    private final Expression right;
    public TimesExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();

        try {
            double result = leftValue.extractValueWithExpectation(Double.class) * rightValue.extractValueWithExpectation(Double.class);
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }
        catch (Exception e) {
            return new EffectiveValueImpl((CellType.UNKNOWN), "NaN" );
        }
    }

    @Override
    public List<Expression> getExpressions() {
        List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(left);
        expressions.add(right);
        return expressions;
    }

}
