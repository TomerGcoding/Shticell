package com.shticell.engine.dto;

import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.sheet.coordinate.CoordinateFactory;
import com.shticell.engine.sheet.impl.SheetProperties;

import java.io.Serializable;
import java.util.Map;

public class SheetDTO implements Serializable {
    private final Map<Coordinate, CellDTO> activeCells;
    private final Map<String, RangeDTO> activeRanges;
    private final int currVersion;
    private final String sheetName;
    private final SheetProperties properties;

    public SheetDTO(Map<Coordinate, CellDTO> activeCells,
                    Map <String, RangeDTO> activeRanges,
                    int currVersion,
                    String sheetName,
                    SheetProperties properties) {
        this.properties = properties;
        this.activeCells = activeCells;
        this.activeRanges = activeRanges;
        this.currVersion = currVersion;
        this.sheetName = sheetName;
    }

    public Map<Coordinate, CellDTO> getActiveCells() {
        return activeCells;
    }


    public Map<String, RangeDTO> getActiveRanges() { return activeRanges; }


    public CellDTO getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    public int getCurrVersion() {
        return currVersion;
    }

    public String getSheetName() {
        return sheetName;
    }

    public SheetProperties getProperties() {
        return properties;
    }
}
