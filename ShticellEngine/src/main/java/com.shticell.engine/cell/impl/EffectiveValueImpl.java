package com.shticell.engine.cell.impl;

import com.shticell.engine.cell.api.EffectiveValue;

import java.io.Serializable;
import java.util.Objects;

public class EffectiveValueImpl implements EffectiveValue, Serializable {

    private final CellType cellType;
    private final Object value;

    public EffectiveValueImpl(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectiveValueImpl that = (EffectiveValueImpl) o;
        return cellType == that.cellType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType, value);
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (type == Integer.class && cellType == CellType.NUMERIC && value instanceof Double) {
            Double doubleValue = (Double) value;
            if (doubleValue % 1 == 0) {
                return type.cast(doubleValue.intValue());  // Cast Double to Integer if it's effectively an integer
            }
        }

        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }

        throw new IllegalArgumentException("Cannot cast " + value + " to " + type.getSimpleName());
    }
}
