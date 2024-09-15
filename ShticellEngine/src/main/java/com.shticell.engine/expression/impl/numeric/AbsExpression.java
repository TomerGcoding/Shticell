package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;

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
        List<Expression> expressions = new ArrayList<>();
        expressions.add(expression);
        return expressions;
    }

}
