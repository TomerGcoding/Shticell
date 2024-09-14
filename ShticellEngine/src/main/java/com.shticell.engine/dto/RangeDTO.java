package com.shticell.engine.dto;

import com.shticell.engine.cell.api.Cell;
import com.sun.codemodel.JForEach;

import java.util.ArrayList;
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


    public List<String> getCellIds() {
        List <String> cellIds = new ArrayList<>();
        for(CellDTO cell: cells){
            cellIds.add(cell.getId());
        }
        return cellIds;
    }
}
