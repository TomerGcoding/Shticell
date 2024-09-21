package com.shticell.engine.dto;

import java.util.ArrayList;
import java.util.List;

import static com.shticell.engine.dto.DTOCreator.cellToDTO;

public class RangeDTO {
    private final String name;
    private final List<CellDTO> cells;
    private final List<String> cellsIdInRange;

    public RangeDTO(String name, List<CellDTO> cells, List<String> cellsIdInRange ) {
        this.name = name;
        this.cells = cells;
        this.cellsIdInRange = cellsIdInRange;
    }

    public String getName() {
        return name;
    }

    public List<CellDTO> getCells() {
        return cells;
    }

    public List<String> getCellsIdInRange() {
        return cellsIdInRange;
    }


}
