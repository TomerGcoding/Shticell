package com.shticell.engine.cell.api;

import com.shticell.engine.cell.impl.CellType;

public interface EffectiveValue {
    CellType getCellType();
    Object getValue();
    <T> T extractValueWithExpectation(Class<T> type);
}
