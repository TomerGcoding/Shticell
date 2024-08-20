package engine.expression.api;


import engine.sheet.cell.api.CellType;
import engine.sheet.cell.api.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
    CellType getFunctionResultType();
}
