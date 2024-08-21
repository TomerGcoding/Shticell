package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.cell.impl.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class ModuloExpression implements Expression {
    private final Expression left;
    private final Expression right;
    private boolean isInt = true;
    public ModuloExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        // do some checking... error handling...
        try {
            int result = leftValue.extractValueWithExpectation(Integer.class) % rightValue.extractValueWithExpectation(Integer.class);
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }
        catch (IllegalArgumentException e) {
            isInt = false;
            return new EffectiveValueImpl(CellType.STRING, "NaN");
        }

    }

    @Override
    public CellType getFunctionResultType () {return CellType.NUMERIC; }
}
