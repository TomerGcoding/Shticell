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

    private List<String> cellsIdInRange;
    private List<Cell> cellsInRange;
     private final String name;
    private List<String> influenceOnCells;

    public RangeImpl(String name, String startCellId, String endCellId, Sheet sheet) {
        this.name = name;
        generateCellIdsInRange(startCellId, endCellId);
        influenceOnCells = new ArrayList<>();
    }

    private void generateCellIdsInRange(String startCellId, String endCellId) {
        int[] startIndex = CoordinateFormatter.cellIdToIndex(startCellId);
        int[] endIndex = CoordinateFormatter.cellIdToIndex(endCellId);

        int startRow = Math.min(startIndex[0], endIndex[0]);
        int endRow = Math.max(startIndex[0], endIndex[0]);
        int startCol = Math.min(startIndex[1], endIndex[1]);
        int endCol = Math.max(startIndex[1], endIndex[1]);

        this.cellsIdInRange = new ArrayList<>();

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                String cellId = CoordinateFormatter.indexToCellId(row, col);
                this.cellsIdInRange.add(cellId);
            }
        }

    }

    public List<Cell> generateCells(Sheet sheet) {
        List cellList = new ArrayList<>();
        for (String cellId : cellsIdInRange) {
            Coordinate coordinate = CoordinateFactory.createCoordinate(cellId);
            Cell cell = sheet.getCell(coordinate);


            if (cell == null) {
                int[] indices = CoordinateFormatter.cellIdToIndex(cellId);
                cell = new CellImpl(indices[0], indices[1], "", sheet.getVersion(), sheet);
                sheet.addCell(coordinate, cell); // Add new cell to the sheet
            }

            cellList.add(cell);
        }
        return cellList;

    }


//    @Override
//    public Double calculateAverage(Sheet sheet) {
//        if (!cellsInRange.isEmpty())
//            return calculateSum(sheet)/cellsInRange.size();
//        return 0.0;
//    }
//
//    @Override
//    public Double calculateSum(Sheet sheet) {
//        Double result = 0.0;
//        for (Cell cell : cellsInRange) {
//            if(cell.getEffectiveValue().getCellType().equals(CellType.NUMERIC))
//                 result += cell.getEffectiveValue().extractValueWithExpectation(Double.class);
//        }
//        return result;
//    }


    @Override
    public List<EffectiveValue> getRangeValues(Sheet sheet) {
        this.cellsInRange = generateCells(sheet);
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
        if (!influenceOnCells.contains(cellId))
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
