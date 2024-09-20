package com.shticell.engine.range;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.sheet.coordinate.CoordinateFactory;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RangeImpl implements Range, Serializable {

    private final String startCellId;
    private final String endCellId;
    private final List<Cell> cellsInRange;
    private final String name;
    private List<String> influenceOnCells = new ArrayList<>();

    public RangeImpl(String name, String startCellId, String endCellId, Sheet sheet) {
        this.name = name;
        this.startCellId = startCellId;
        this.endCellId = endCellId;
        this.cellsInRange = generateCells(sheet);
    }


    public List<Cell> generateCells(Sheet sheet) {
        int[] startIndex = CoordinateFormatter.cellIdToIndex(startCellId);
        int[] endIndex = CoordinateFormatter.cellIdToIndex(endCellId);

        int startRow = Math.min(startIndex[0], endIndex[0]);
        int endRow = Math.max(startIndex[0], endIndex[0]);
        int startCol = Math.min(startIndex[1], endIndex[1]);
        int endCol = Math.max(startIndex[1], endIndex[1]);

        List<Cell> cellsInRange = new ArrayList<>();

        // Iterate over rows and columns to generate the cells
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                Cell cell = sheet.getCell(coordinate);

                // If the cell doesn't exist, create it
                if (cell == null) {
                    cell = new CellImpl(row, col, "", sheet.getVersion(), sheet);
                    sheet.addCell(coordinate, cell); // Add new cell to the sheet
                }

                cellsInRange.add(cell);
            }
        }

        return cellsInRange;
    }

    @Override
    public List<Cell> getCells() {
        return cellsInRange;
    }

//    @Override
//    public Double calculateAverage() {
//        if (!cellsInRange.isEmpty())
//            return calculateSum()/cellsInRange.size();
//        return 0.0;
//    }
//
//    @Override
//    public Double calculateSum() {
//        Double result = 0.0;
//        for (Cell cell : cellsInRange) {
//            if(cell.getEffectiveValue().getCellType().equals(CellType.NUMERIC))
//                result += cell.getEffectiveValue().extractValueWithExpectation(Double.class);
//        }
//        return result;
//    }

    @Override
    public List<EffectiveValue> getRangeValues() {
        List <EffectiveValue> values = new ArrayList<>();
        for(Cell cell : cellsInRange){
            values.add(cell.getEffectiveValue());
        }
        return values;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addInfluence(String cellId) {
        influenceOnCells.add(cellId);
    }

    @Override
    public void removeInfluence(String cellId) {
        influenceOnCells.remove(cellId);;
    }
    @Override
    public List<String> getInfluenceOnCells() {
        return influenceOnCells;
    }
}