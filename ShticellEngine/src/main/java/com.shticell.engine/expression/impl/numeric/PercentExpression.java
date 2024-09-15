package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.*;

import java.util.ArrayList;
import java.util.List;

public class PercentExpression extends NumericExpression {
    private final Expression part;
    private final Expression whole;

    public PercentExpression(Expression left, Expression right) {
        this.part = left;
        this.whole = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue partValue = part.eval();
        EffectiveValue wholeValue = whole.eval();

        try {
            double result = partValue.extractValueWithExpectation(Double.class) * wholeValue.extractValueWithExpectation(Double.class) / 100;
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }
        catch (Exception e) {
            return new EffectiveValueImpl((CellType.UNKNOWN), "NaN" );
        }
    }

    @Override
    public List<Expression> getExpressions() {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(part);
        expressions.add(whole);
        return expressions;
    }
}
