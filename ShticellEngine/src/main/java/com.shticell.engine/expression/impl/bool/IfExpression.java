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
        // Evaluate the condition
        EffectiveValue conditionValue = condition.eval();

        // Check if the condition is a Boolean
        if (conditionValue.getCellType() != CellType.BOOLEAN) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "!UNDEFINED!");
        }

        // Determine the outcome based on the condition
        boolean conditionResult = (Boolean) conditionValue.getValue();
        EffectiveValue result;

        if (conditionResult) {
            result = thenExpression.eval();
        } else {
            result = elseExpression.eval();
        }

        // Ensure both expressions return the same type
        if (!result.getCellType().equals(result.getCellType())) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "!UNDEFINED!");
        }

        return result;
    }

    @Override
    public CellType getFunctionResultType() {
        // Return the result type of the thenExpression and elseExpression
        return thenExpression.getFunctionResultType();
    }

    @Override
    public boolean isDepndsOnSomeCell() {
        return false;
    }
}
