package engine.sheet.api;

import engine.sheet.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.impl.SheetImpl;
import engine.sheet.impl.SheetProperties;

import java.util.Map;

public interface Sheet {
    int getVersion();
    Cell getCell(int row, int column);
    void setCell(int row, int column, String value);
    void setCell(String id, String value);
    String getSheetName();
    Map<Coordinate, Cell> getCells();
    SheetProperties getProperties();
}
