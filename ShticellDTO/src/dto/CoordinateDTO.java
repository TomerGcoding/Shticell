package dto;

public class CoordinateDTO {
    private int row;
    private int column;

    public CoordinateDTO() {
    }

    public CoordinateDTO(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return row + "," + column;  // Format the coordinate as a string
    }

    public static int[] cellIdToIndex(String cellId) {
        cellId = cellId.toUpperCase();
        if (!cellId.matches("^[A-Z]+\\d+$")) {
            throw new IllegalArgumentException("Invalid cell ID format: " + cellId);
        }

        int i = 0;
        while (i < cellId.length() && Character.isLetter(cellId.charAt(i))) {
            i++;
        }

        String columnPart = cellId.substring(0, i);
        String rowPart = cellId.substring(i);

        int rowIndex = Integer.parseInt(rowPart) - 1;

        int columnIndex = getColumnIndex(columnPart);

        return new int[]{rowIndex, columnIndex};
    }
    public static int getColumnIndex(String columnLabel) {
        int columnIndex = 0;

        for (int i = 0; i < columnLabel.length(); i++) {
            columnIndex *= 26;
            columnIndex += (columnLabel.charAt(i) - 'A' + 1);
        }

        return columnIndex - 1;  // Make it 0-based
    }

}
