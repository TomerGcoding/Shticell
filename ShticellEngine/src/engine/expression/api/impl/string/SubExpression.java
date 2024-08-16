package engine.expression.api.impl.string;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class SubExpression implements Expression {
    private Expression source;
    private Expression startIndex;
    private Expression endIndex;
    public SubExpression(Expression source, Expression startIndex, Expression endIndex) {
        this.source = source;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    public EffectiveValue eval() {
        EffectiveValue sourceString = source.eval();
        EffectiveValue start = startIndex.eval();
        EffectiveValue end = endIndex.eval();
        // do some checking... error handling...
        String result = sourceString.extractValueWithExpectation(String.class).substring(
                start.extractValueWithExpectation(Integer.class),end.extractValueWithExpectation(Integer.class));

        return new EffectiveValueImpl(CellType.STRING, result);
    }

}
