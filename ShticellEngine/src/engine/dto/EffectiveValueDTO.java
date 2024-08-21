package engine.dto;

import engine.sheet.cell.api.CellType;

public class EffectiveValueDTO {

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
}
