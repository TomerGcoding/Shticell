package engine.dto;

import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.impl.SheetProperties;

import java.io.Serializable;
import java.util.Map;

public class SheetDTO implements Serializable {
    private final Map<Coordinate, CellDTO> activeCells;
    private final int currVersion;
    private final String sheetName;
    private final SheetProperties properties;

    public SheetDTO(Map<Coordinate, CellDTO> activeCells,
                    int currVersion,
                    String sheetName,
                    SheetProperties properties) {
        this.properties = properties;
        this.activeCells = activeCells;
        this.currVersion = currVersion;
        this.sheetName = sheetName;
    }

    public Map<Coordinate, CellDTO> getActiveCells() {
        return activeCells;
    }

    public CellDTO getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    public int getCurrVersion() {
        return currVersion;
    }

    public String getSheetName() {
        return sheetName;
    }

    public SheetProperties getProperties() {
        return properties;
    }
}
