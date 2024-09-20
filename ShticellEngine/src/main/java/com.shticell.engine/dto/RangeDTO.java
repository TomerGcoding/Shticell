package com.shticell.engine.dto;

import java.util.ArrayList;
import java.util.List;

import static com.shticell.engine.dto.DTOCreator.cellToDTO;

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
