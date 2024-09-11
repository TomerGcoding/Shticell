package com.shticell.engine.dto;

import com.shticell.engine.cell.impl.CellType;

import java.io.Serializable;

public class EffectiveValueDTO implements Serializable {

    private final CellType cellType;
    private final Object value;

    public EffectiveValueDTO(CellType cellType, Object value) {
        this.cellType = cellType;
        this.value = value;
    }

    public CellType getCellType() {
        return cellType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {return value.toString();}
}
