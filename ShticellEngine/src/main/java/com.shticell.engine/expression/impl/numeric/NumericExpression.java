package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;

import java.util.List;

public abstract class NumericExpression implements Expression {

    @Override
    public CellType getFunctionResultType () {return CellType.NUMERIC; }
    public abstract List<Expression> getExpressions();
}
