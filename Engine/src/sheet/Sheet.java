package sheet;
import cell.Cell;

import java.util.HashMap;
import java.util.Map;

public class Sheet {
    private int length;
    private int width;
    private int columnSize;
    private int rowSize;
    private Map<String, Cell> cells;

    public Sheet(int length, int width) {
        this.length = length;
        this.width = width;
        this.columnSize = width;
        this.rowSize = length;
        this.cells = new HashMap<>();
    }

    public void setCell(String position, Cell cell) {
        cells.put(position, cell);
    }

    public Cell getCell(String position) {
        return cells.get(position);
    }

    public void printSheet() {
        // Print the column headers (A, B, C, ..., Z, AA, AB, ...)
        for (int i = 0; i < columnSize; i++) {
            if (i > 0) {
                System.out.print("|");
            }
            System.out.print(getColumnName(i));
        }
        System.out.println();

        // Print each row
        for (int row = 1; row <= rowSize; row++) {
            for (int col = 0; col < columnSize; col++) {
                String cellPosition = getColumnName(col) + row;
                Cell cell = cells.get(cellPosition);

                if (col > 0) {
                    System.out.print("|");
                }

                if (cell != null) {
                    System.out.print(cell.getValue());
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    private String getColumnName(int index) {
        StringBuilder columnName = new StringBuilder();
        while (index >= 0) {
            columnName.insert(0, (char) ('A' + index % 26));
            index = index / 26 - 1;
        }
        return columnName.toString();
    }
}
