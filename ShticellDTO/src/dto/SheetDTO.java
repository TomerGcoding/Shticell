package dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SheetDTO implements Serializable {
    private Map<CoordinateDTO, CellDTO> activeCells;
    private Map<String, RangeDTO> activeRanges;
    private int currVersion;
    private String sheetName;
    private SheetPropertiesDTO properties;

    public SheetDTO() {
        this.activeCells = null;
        this.activeRanges = null;
        this.currVersion = 0;
        this.sheetName = null;
        this.properties = null;
    }

    public SheetDTO(Map<CoordinateDTO, CellDTO> activeCells,
                    Map <String, RangeDTO> activeRanges,
                    int currVersion,
                    String sheetName,
                    SheetPropertiesDTO properties) {
        this.activeCells = activeCells;
        this.activeRanges = activeRanges;
        this.currVersion = currVersion;
        this.sheetName = sheetName;
        this.properties = properties;

    }

    public SheetPropertiesDTO getProperties() {
        return properties;
    }


    public Map<CoordinateDTO, CellDTO> getActiveCells() {
        return activeCells;
    }


    public Map<String, RangeDTO> getActiveRanges() { return activeRanges; }


    public CellDTO getCell(int row, int column) {
        for (CoordinateDTO coordinate : activeCells.keySet()) {
            if (coordinate.getRow() == row && coordinate.getColumn() == column) {
                return activeCells.get(coordinate);
            }
        }
        return null;
    }

    public CellDTO getCell(String cellId)
    {
        int[] index = CoordinateDTO.cellIdToIndex(cellId);
        return getCell(index[0], index[1]);
    }

    public int getCurrVersion() {
        return currVersion;
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getNumRows() {
        return properties.getNumRows();
    }

    public int getNumColumns() {
        return properties.getNumCols();
    }

    public int getColWidth() {
        return properties.getColWidth();
    }

    public int getRowHeight() {
        return properties.getRowHeight();
    }

    public List<String> getUniqueColumnValues(String columnId) {
        List <String> values = new ArrayList<>();
        int column = CoordinateDTO.cellIdToIndex(columnId + "1")[1];
        for (CellDTO cell : activeCells.values()) {
            if (cell.getColumn() == column && !values.contains(cell.getEffectiveValue().getValue().toString())) {
                values.add(cell.getEffectiveValue().getValue().toString());
            }
        }
        System.out.println("unique values in getUnique value: " + values);
        return values;
    }

}
