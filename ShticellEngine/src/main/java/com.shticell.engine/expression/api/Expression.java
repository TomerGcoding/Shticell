package com.shticell.engine.expression.api;


import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;

public interface Expression {
    EffectiveValue eval();
    CellType getFunctionResultType();

    boolean isDepndsOnSomeCell();
}
