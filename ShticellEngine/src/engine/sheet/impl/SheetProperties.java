package engine.sheet.impl;

public class SheetProperties {
    private final int numCols;
    private final int numRows;
    private final int colWidth;
    private final int rowHeight;

    public SheetProperties(int numCols, int numRows, int colWidth, int rowHeight) {
        this.numCols = numCols;
        this.numRows = numRows;
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getColWidth() {
        return colWidth;
    }

    public int getRowHeight() {
        return rowHeight;
    }

}
