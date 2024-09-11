package com.shticell.engine.expression.impl.string;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;

import java.util.List;

public abstract class StringExpression implements Expression {
    @Override
    public CellType getFunctionResultType () {return CellType.STRING; }

    public abstract List<Expression> getExpressions();
    @Override
    public boolean isDepndsOnSomeCell() {
        return false;
    }
}
