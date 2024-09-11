package com.shticell.engine.cell.impl;

import java.io.Serializable;

public enum CellType implements Serializable {
    NUMERIC(Double.class),
    STRING(String.class),
    BOOLEAN(Boolean.class),
    UNKNOWN(Void.class);

    private final Class<?> type;

    CellType(Class<?> type) {
        this.type = type;
    }

    public boolean isAssignableFrom(Class<?> aType) {
        if (this == NUMERIC && aType == Integer.class) {
            return true;
        }
        return type.isAssignableFrom(aType);
    }
}
