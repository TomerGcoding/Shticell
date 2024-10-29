package com.shticell.engine.utils;

import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.sheet.impl.SheetProperties;
import com.shticell.engine.users.accessPermission.SheetUserAccessManager;
import com.shticell.engine.users.accessPermission.UserAccessPermission;
import dto.*;

import java.io.Serializable;
import java.util.*;

public class DTOCreator implements Serializable {

    public static EffectiveValueDTO effectiveValueToDTO(EffectiveValue effectiveValue) {
        return new EffectiveValueDTO(effectiveValue.toString());
    }

    public static CellDTO cellToDTO(Cell cell) {
        return cellToDTO(cell, new ArrayList<>());
    }

    private static CellDTO cellToDTO(Cell cell, List<Cell> visitedCells) {
        if (visitedCells.contains(cell)) {
            return null;
        }

        visitedCells.add(cell);

        List<String> dependsOn = new ArrayList<>();
        if (cell.getDependsOn() != null) {
            for (Cell dependsOnCell : cell.getDependsOn()) {
                CellDTO dependsOnDTO = cellToDTO(dependsOnCell, visitedCells);
                if (dependsOnDTO != null) {
                    dependsOn.add(dependsOnDTO.getId());
                }
            }
        }

        List<String> influencingOn = new ArrayList<>();
        if (cell.getInfluencingOn() != null) {
            for (Cell influencingOnCell : cell.getInfluencingOn()) {
                CellDTO influencingOnDTO = cellToDTO(influencingOnCell, visitedCells);
                if (influencingOnDTO != null) {
                    influencingOn.add(influencingOnDTO.getId());
                }
            }
        }

        visitedCells.remove(cell);
        CoordinateDTO coordinateDTO = coordinateToDTO(cell.getCoordinate());

        return new CellDTO(cell.getId(),
                coordinateDTO,
                cell.getOriginalValue(),
                effectiveValueToDTO(cell.getEffectiveValue()),
                cell.getVersion(),
                dependsOn,
                influencingOn);
    }

    public static SheetDTO sheetToDTO(Sheet sheet) {
        Map<CoordinateDTO, CellDTO> cells = new HashMap<>();
        Map<String, RangeDTO> ranges = new HashMap<>();
        for (Map.Entry<Coordinate, Cell> entry : sheet.getCells().entrySet()) {
            CellDTO cellDTO = cellToDTO(entry.getValue());
            CoordinateDTO coorDTO = coordinateToDTO(entry.getKey());
            if (cellDTO != null) {
                cells.put(coorDTO, cellDTO);
            }
        }
        for (Range range : sheet.getRanges().values()) {
            RangeDTO rangeDTO = rangeToDTO(range, sheet);
            ranges.put(rangeDTO.getName(), rangeDTO);
        }
        SheetUsersAccessDTO sheetUsersAccessDTO = sheetUsersAccessToDTO(sheet.getSheetUserAccessManager());
        SheetPropertiesDTO propertiesDTO = SheetPropertiesToDTO(sheet.getProperties());
        return new SheetDTO(cells,
                ranges,
                sheet.getVersion(),
                sheet.getSheetName(),
                propertiesDTO,
                sheetUsersAccessDTO);
    }

    private static SheetPropertiesDTO SheetPropertiesToDTO(SheetProperties properties) {
        return new SheetPropertiesDTO (properties.getNumRows(), properties.getNumCols(), properties.getRowHeight(), properties.getColWidth());
    }

    private static CoordinateDTO coordinateToDTO(Coordinate coordinate) {
        return new CoordinateDTO(coordinate.getRow(), coordinate.getColumn());
    }

    public static RangeDTO rangeToDTO(Range range, Sheet sheet) {
        List<CellDTO> cellDTOList = new ArrayList<>();
        List <String> Ids = new ArrayList<>();
        for (Cell cell : range.getCells(sheet)) {
            cellDTOList.add(cellToDTO(cell));
            Ids.add(cell.getId());
        }
        return new RangeDTO(range.getName(), cellDTOList, Ids);
    }

    public static UserAccessDTO userAccessToDTO (UserAccessPermission userAccess) {
        return new UserAccessDTO(userAccess.getUsername(), userAccess.getAccessPermissionType().toString(), userAccess.getAccessPermissionStatus().toString());
    }

    public static SheetUsersAccessDTO sheetUsersAccessToDTO (SheetUserAccessManager sheetUserAccessManager) {
        Set<UserAccessDTO> userAccessDTOSet = new HashSet<>();
        for (UserAccessPermission userAccess : sheetUserAccessManager.getSheetUserAccessManager(). values()) {
            userAccessDTOSet.add(userAccessToDTO(userAccess));
        }
        return new SheetUsersAccessDTO(userAccessDTOSet);
    }

}
