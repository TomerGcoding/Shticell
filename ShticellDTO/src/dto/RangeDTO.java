package dto;

import java.io.Serializable;
import java.util.List;

public class RangeDTO implements Serializable {
    private String name;
    private List<CellDTO> cells;
    private List<String> cellsIdInRange;

    public RangeDTO(String name, List<CellDTO> cells, List<String> cellsIdInRange ) {
        this.name = name;
        this.cells = cells;
        this.cellsIdInRange = cellsIdInRange;
    }

    public RangeDTO() {
        this.name = null;
        this.cells = null;
        this.cellsIdInRange = null;
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
