package engine.dto;

import engine.sheet.cell.api.Cell;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;

import java.util.List;

public class CellDTO {
    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValueDTO effectiveValue;
    private final int version;
    private final List<CellDTO> dependsOn;
    private final List<CellDTO> influencingOn;

    public CellDTO(Coordinate coordinate,
                   String originalValue,
                   EffectiveValueDTO effectiveValue,
                   int version,
                   List<CellDTO> dependsOn,
                   List<CellDTO> influencingOn) {
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.dependsOn = dependsOn;
        this.influencingOn = influencingOn;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public EffectiveValueDTO getEffectiveValue() {
        return effectiveValue;
    }

    public int getVersion() {
        return version;
    }

    public List<CellDTO> getDependsOn() {
        return dependsOn;
    }

    public List<CellDTO> getInfluencingOn() {
        return influencingOn;
    }
}
