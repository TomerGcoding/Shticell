package com.shticell.engine.range;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.sheet.coordinate.CoordinateFactory;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import com.shticell.engine.sheet.impl.SheetProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RangeImpl implements Range, Serializable {

    private final String startCellId;
    private final String endCellId;
    //  private List<Cell> cellsInRange;
    private final List<String> cellsIdsInRange = new ArrayList<>();
    private final String name;
    private List<String> influenceOnCells = new ArrayList<>();

    public RangeImpl(String name, String startCellId, String endCellId, Sheet sheet) {
        this.name = name;
        this.startCellId = startCellId;
        this.endCellId = endCellId;
        generateCellsId(sheet);
    }

    private void generateCellsId(Sheet sheet) {
        int[] startIndex = CoordinateFormatter.cellIdToIndex(startCellId);
        int[] endIndex = CoordinateFormatter.cellIdToIndex(endCellId);

        int startRow = Math.min(startIndex[0], endIndex[0]);
        int endRow = Math.max(startIndex[0], endIndex[0]);
        int startCol = Math.min(startIndex[1], endIndex[1]);
        int endCol = Math.max(startIndex[1], endIndex[1]);

        if (!sheet.getProperties().isCoordinateLegal(startRow, startCol) ||
                !sheet.getProperties().isCoordinateLegal(endRow, endCol)) {
            throw new IllegalArgumentException();
        }

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                Coordinate coordinate = CoordinateFactory.createCoordinate(row, col);
                Cell cell = sheet.getCell(coordinate);

                if (cell == null) {
                    cell = new CellImpl(row, col, "", sheet.getVersion(), sheet,"");
                    sheet.addCell(coordinate, cell);
                }

                cellsIdsInRange.add(cell.getId());
            }
        }
    }

    @Override
    public List<Cell> getCells(Sheet sheet) {
        List<Cell> cells = new ArrayList<>();
        for (String cellId : cellsIdsInRange) {
            cells.add(sheet.getCell(cellId));
        }
        return cells;
    }


    @Override
    public List<EffectiveValue> getRangeValues(Sheet sheet) {
        List <EffectiveValue> values = new ArrayList<>();
        List <Cell> cellsInRange = getCells(sheet);
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