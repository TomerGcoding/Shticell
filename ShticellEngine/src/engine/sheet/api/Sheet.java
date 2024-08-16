package engine.sheet.api;

import engine.sheet.cell.api.Cell;
import engine.sheet.impl.SheetImpl;

public interface Sheet {
    int getVersion();
    Cell getCell(int row, int column);
    void setCell(int row, int column, String value);
    String getSheetName();
    int getRowCount();
    int getColumnCount();
    int getColumnWidth();
    int getRowHeight();
}
