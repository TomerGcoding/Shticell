package engine.dto;

import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class CellDTO {
    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private final int version;
    private final List<CellDTO> dependsOn;
    private final List<CellDTO> influencingOn;

    public CellDTO(Coordinate coordinate, String originalValue, EffectiveValue effectiveValue,
                   int version, List<CellDTO> dependsOn, List<CellDTO> influencingOn) {
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.dependsOn = dependsOn;
        this.influencingOn = influencingOn;
    }
    public CellDTO(Cell cell) {
        this.coordinate = cell.getCoordinate();
        this.originalValue = cell.getOriginalValue();
        this.effectiveValue = cell.getEffectiveValue();
        this.version = cell.getVersion();
        this.dependsOn = new ArrayList<CellDTO>();

    }
}
