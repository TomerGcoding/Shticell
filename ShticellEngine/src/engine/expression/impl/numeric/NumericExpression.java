package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;

public abstract class NumericExpression implements Expression {
    @Override
    public CellType getFunctionResultType () {return CellType.NUMERIC; }
}
