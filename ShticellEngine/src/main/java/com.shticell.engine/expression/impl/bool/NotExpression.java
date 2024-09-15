package com.shticell.engine.expression.impl.bool;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class NotExpression implements Expression {
    private final Expression expression;

    public NotExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue value = expression.eval();

        try {
            // Ensure the expression evaluates to a Boolean
            Boolean result = !value.extractValueWithExpectation(Boolean.class);
            return new EffectiveValueImpl(CellType.BOOLEAN, result);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "!UNDEFINED!");
        }
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.BOOLEAN;
    }

    @Override
    public boolean isDepndsOnSomeCell() {
        return false;
    }
}

//    @Override
//    public List<Expression> getExpressions() {
//        List<Expression> expressions = new ArrayList<>();
//        expressions.add(expression);
//        return expressions;
//    }
//}