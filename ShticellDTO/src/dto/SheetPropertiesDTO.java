package dto;

import java.io.Serializable;

public class SheetPropertiesDTO implements Serializable {

    private int numCols;
    private int numRows;
    private int colWidth;
    private int rowHeight;

    public SheetPropertiesDTO() {
        this.numCols = 0;
        this.numRows = 0;
        this.colWidth = 0;
        this.rowHeight = 0;
    }

    public SheetPropertiesDTO(int numRows, int numCols, int rowHeight, int colWidth) {
        this.numRows = numRows;
        this.numCols = numCols;
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
