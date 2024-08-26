package engine.sheet.cell.impl;

import engine.sheet.cell.api.EffectiveValue;

import java.io.Serializable;
import java.util.Objects;

public class EffectiveValueImpl implements EffectiveValue, Serializable {

    private CellType cellType;
    private Object value;

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
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        }

        throw new IllegalArgumentException("exception in extractValueWithException");
    }
}

