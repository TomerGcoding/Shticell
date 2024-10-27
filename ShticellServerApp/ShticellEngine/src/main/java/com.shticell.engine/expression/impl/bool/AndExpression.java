package com.shticell.engine.expression.impl.bool;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.expression.api.Expression;
public class AndExpression implements Expression {
    private final Expression left;
    private final Expression right;
    public AndExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        try {
            Boolean result = leftValue.extractValueWithExpectation(Boolean.class) && rightValue.extractValueWithExpectation(Boolean.class);
            return new EffectiveValueImpl(CellType.BOOLEAN, result);
        } catch (Exception e) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "!UNDEFINED!");
        }
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