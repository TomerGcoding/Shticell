package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.cell.impl.CellType;

import java.util.List;

public abstract class NumericExpression implements Expression {

    @Override
    public CellType getFunctionResultType () {return CellType.NUMERIC; }
    public abstract List<Expression> getExpressions();
}
