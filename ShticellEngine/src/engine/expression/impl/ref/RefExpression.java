package engine.expression.impl.ref;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class RefExpression implements Expression {
    private final String cellId;

    public RefExpression(String cellId) {
        this.cellId = cellId;
    }

    @Override
    public EffectiveValue eval() {return new EffectiveValueImpl(CellType.NUMERIC,cellId);}
    }
}