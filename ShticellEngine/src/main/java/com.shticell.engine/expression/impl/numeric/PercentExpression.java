package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.expression.api.Expression;

import static com.shticell.engine.cell.impl.CellType.NUMERIC;

public class PercentExpression implements Expression {
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

        if (partValue.getCellType()==NUMERIC && wholeValue.getCellType()==NUMERIC) {
          return new EffectiveValueImpl(NUMERIC, (Double)partValue.getValue() * (Double)wholeValue.getValue()/ 100);
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
