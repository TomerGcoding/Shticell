package engine.expression.api;


import engine.cell.impl.CellType;
import engine.cell.api.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
    CellType getFunctionResultType();
}
