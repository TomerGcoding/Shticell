package engine.dto;

import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTOCreator {

    public static EffectiveValueDTO effectiveValueToDTO(EffectiveValue effectiveValue) {
        return new EffectiveValueDTO(effectiveValue.getCellType(),effectiveValue.getValue());
    }

    public static CellDTO cellToDTO(Cell cell) {
        List<CellDTO> dependsOn = new ArrayList<CellDTO>();
        if (cell.getDependsOn() != null) {
            for (Cell dependsOnCell : cell.getDependsOn()) {
                dependsOn.add(cellToDTO(dependsOnCell));
            }
        }

        List<CellDTO> influencingOn = new ArrayList<CellDTO>();
        if (cell.getInfluencingOn() != null) {
            for (Cell influencingOnCell : cell.getInfluencingOn()) {
                influencingOn.add(cellToDTO(influencingOnCell));
            }
        }
        return new CellDTO(cell.getId(),
                cell.getCoordinate(),
                cell.getOriginalValue(),
                effectiveValueToDTO(cell.getEffectiveValue()),
                cell.getVersion(),
                dependsOn,
                influencingOn);
    }

    public static SheetDTO sheetToDTO(Sheet sheet) {
        Map<Coordinate,CellDTO> cells = new HashMap<Coordinate,CellDTO>();
        for (Map.Entry<Coordinate,Cell> entry : sheet.getCells().entrySet()) {
            cells.put(entry.getKey(), cellToDTO(entry.getValue()));
        }
        return new SheetDTO(cells,
                sheet.getVersion(),
                sheet.getSheetName(),
                sheet.getProperties());
    }
}
