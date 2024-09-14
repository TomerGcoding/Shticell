package com.shticell.engine.dto;

import com.shticell.engine.cell.api.Cell;
import com.sun.codemodel.JForEach;
import java.util.List;

public class RangeDTO {
    private final String name;
    private final List<CellDTO> cells;

    public RangeDTO(String name, List<CellDTO> cells) {
        this.name = name;
        this.cells = cells;
    }

    public String getName() {
        return name;
    }

    public List<CellDTO> getCells() {
        return cells;
    }


}
