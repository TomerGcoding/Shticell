package com.shticell.engine.expression.impl.bool;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.expression.api.Expression;

public class EqualsExpression implements Expression {
    private final Expression left;
    private final Expression right;

    public EqualsExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();

        if (leftValue.getCellType().equals(rightValue.getCellType())) {
            if (leftValue.getValue().equals(rightValue.getValue())) {
                return new EffectiveValueImpl(CellType.BOOLEAN, true);
            } else {
                return new EffectiveValueImpl(CellType.BOOLEAN, false);
            }
        }
        else
            return new EffectiveValueImpl(CellType.UNKNOWN, "!UNDEFINED!");
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
