package engine.expression.api;


import engine.sheet.cell.api.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
}
