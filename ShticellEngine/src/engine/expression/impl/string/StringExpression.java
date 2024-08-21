package engine.expression.impl.string;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;

public abstract class StringExpression implements Expression {
    @Override
    public CellType getFunctionResultType () {return CellType.STRING; }
}
