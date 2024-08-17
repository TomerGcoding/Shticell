package engine.dto;

import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.impl.SheetImpl;
import engine.sheet.impl.SheetProperties;

import java.util.Map;

public class SheetDTO {
    private final Map<Coordinate, Cell> activeCells;
    private final int currVersion;
    private final String sheetName;
    private final SheetProperties properties;

    public SheetDTO(Map<Coordinate, Cell> activeCells,int currVersion,String sheetName,
                    SheetProperties properties) {
        this.properties = properties;
        this.activeCells = activeCells;
        this.currVersion = currVersion;
        this.sheetName = sheetName;
    }

    public Map<Coordinate, Cell> getActiveCells() {
        return activeCells;
    }
    public Cell getCell(int row, int column) {
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
