package com.shticell.engine.expression.impl.bool;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.expression.api.Expression;

public class IfExpression implements Expression {

    private final Expression condition;
    private final Expression thenExpression;
    private final Expression elseExpression;

    public IfExpression(Expression condition, Expression thenExpression, Expression elseExpression) {
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue conditionValue = condition.eval();

        if (conditionValue.getCellType() != CellType.BOOLEAN) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "!UNDEFINED!");
        }

        boolean conditionResult = (Boolean) conditionValue.getValue();
        EffectiveValue result;

        if (conditionResult) {
            result = thenExpression.eval();
        } else {
            result = elseExpression.eval();
        }

        if (!result.getCellType().equals(result.getCellType())) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "!UNDEFINED!");
        }

        return result;
    }

    @Override
    public CellType getFunctionResultType() {
        return thenExpression.getFunctionResultType();
    }

    @Override
    public boolean isDepndsOnSomeCell() {
        return false;
    }
}
