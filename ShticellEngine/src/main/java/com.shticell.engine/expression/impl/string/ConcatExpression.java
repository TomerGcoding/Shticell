package com.shticell.engine.expression.impl.string;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;

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

        try {
            String result = leftValue.extractValueWithExpectation(String.class) + rightValue.extractValueWithExpectation(String.class);
            return new EffectiveValueImpl(CellType.STRING, result);
        }
        catch (Exception e) {
            return new EffectiveValueImpl((CellType.UNKNOWN), "!UNDEFINED!" );
        }
    }
    @Override
    public List<Expression> getExpressions(){
        List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(left);
        expressions.add(right);
        return expressions;
    }




}
