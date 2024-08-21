package engine.expression.api;


import engine.sheet.cell.impl.CellType;
import engine.sheet.cell.api.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
    CellType getFunctionResultType();
}
