package engine.sheet.coordinate;

public class CoordinateFormatter {
    public static String indexToCellId(int row, int column){
        String columnLabel = getColumnLabel(column);
        int rowLabel = row + 1;  // Rows are 1-based, so add 1 to the row index
        return rowLabel + columnLabel;
    }

    public static int[] cellIdToIndex(String cellId){
        // Find the point where the numbers end and the letters begin
        int i = 0;
        while (i < cellId.length() && Character.isDigit(cellId.charAt(i))) {
            i++;
        }

        // Split the label into row part and column part
        String rowPart = cellId.substring(0, i);
        String columnPart = cellId.substring(i);

        // Convert the row part to an integer and subtract 1 to make it 0-based
        int rowIndex = Integer.parseInt(rowPart) - 1;

        // Convert the column part to an index
        int columnIndex = getColumnIndex(columnPart);

        return new int[]{rowIndex, columnIndex};
    }

    private static int getColumnIndex(String columnLabel) {
        int columnIndex = 0;

        for (int i = 0; i < columnLabel.length(); i++) {
            columnIndex *= 26;
            columnIndex += (columnLabel.charAt(i) - 'A' + 1);
        }

        return columnIndex - 1;  // Make it 0-based
    }

    private static String getColumnLabel(int columnIndex) {
        StringBuilder columnLabel = new StringBuilder();

        while (columnIndex >= 0) {
            int remainder = columnIndex % 26;
            columnLabel.insert(0, (char) ('A' + remainder));
            columnIndex = (columnIndex / 26) - 1;
        }

        return columnLabel.toString();
    }

}
