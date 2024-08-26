package engine.expression.impl.string;

import engine.expression.api.Expression;
import engine.sheet.cell.impl.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class ConcatExpression extends StringExpression {
    private Expression left;
    private Expression right;
    public ConcatExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        // do some checking... error handling...
        String result = leftValue.extractValueWithExpectation(String.class) + rightValue.extractValueWithExpectation(String.class);

        return new EffectiveValueImpl(CellType.STRING, result);
    }
    @Override
    public List<Expression> getExpressions(){
        List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(left);
        expressions.add(right);
        return expressions;
    }



}
