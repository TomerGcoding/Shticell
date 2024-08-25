package engine.expression.impl.string;

import engine.expression.api.Expression;
import engine.sheet.cell.impl.CellType;

import java.util.List;

public abstract class StringExpression implements Expression {
    @Override
    public CellType getFunctionResultType () {return CellType.STRING; }

    public abstract List<Expression> getExpressions();
}
