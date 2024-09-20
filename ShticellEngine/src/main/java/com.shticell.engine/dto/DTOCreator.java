package com.shticell.engine.dto;

import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.sheet.coordinate.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTOCreator {

    public static EffectiveValueDTO effectiveValueToDTO(EffectiveValue effectiveValue) {
        return new EffectiveValueDTO(effectiveValue.getCellType(), effectiveValue.getValue());
    }

    public static CellDTO cellToDTO(Cell cell) {
        return cellToDTO(cell, new ArrayList<>());
    }

    private static CellDTO cellToDTO(Cell cell, List<Cell> visitedCells) {
        if (visitedCells.contains(cell)) {
            // Break the recursion if we've already visited this cell
            return null; // or handle it differently if needed
        }

        visitedCells.add(cell);

        List<CellDTO> dependsOn = new ArrayList<>();
        if (cell.getDependsOn() != null) {
            for (Cell dependsOnCell : cell.getDependsOn()) {
                CellDTO dependsOnDTO = cellToDTO(dependsOnCell, visitedCells);
                if (dependsOnDTO != null) {
                    dependsOn.add(dependsOnDTO);
                }
            }
        }

        List<CellDTO> influencingOn = new ArrayList<>();
        if (cell.getInfluencingOn() != null) {
            for (Cell influencingOnCell : cell.getInfluencingOn()) {
                CellDTO influencingOnDTO = cellToDTO(influencingOnCell, visitedCells);
                if (influencingOnDTO != null) {
                    influencingOn.add(influencingOnDTO);
                }
            }
        }

        // Remove the current cell from the visited list after processing
        visitedCells.remove(cell);

        return new CellDTO(cell.getId(),
                cell.getCoordinate(),
                cell.getOriginalValue(),
                effectiveValueToDTO(cell.getEffectiveValue()),
                cell.getVersion(),
                dependsOn,
                influencingOn);
    }

    public static SheetDTO sheetToDTO(Sheet sheet) {
        Map<Coordinate, CellDTO> cells = new HashMap<>();
        for (Map.Entry<Coordinate, Cell> entry : sheet.getCells().entrySet()) {
            CellDTO cellDTO = cellToDTO(entry.getValue());
            if (cellDTO != null) {
                cells.put(entry.getKey(), cellDTO);
            }
        }
        return new SheetDTO(cells,
                sheet.getVersion(),
                sheet.getSheetName(),
                sheet.getProperties());
    }

    public static RangeDTO rangeToDTO(Range range) {
        List<CellDTO> cellDTOList = new ArrayList<>();
        List <String> Ids = new ArrayList<>();
        for (Cell cell : range.getCells()) {
            cellDTOList.add(cellToDTO(cell));
            Ids.add(cell.getId());
        }
        return new RangeDTO(range.getName(), cellDTOList, Ids);
    }
}
