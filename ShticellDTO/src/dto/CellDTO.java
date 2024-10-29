package dto;

import java.io.Serializable;
import java.util.List;

public class
CellDTO implements Serializable {
    private String ID;
    private CoordinateDTO coordinate;
    private String originalValue;
    private EffectiveValueDTO effectiveValue;
    private int version;
    private List<String> dependsOn;
    private List<String> influencingOn;

    public CellDTO() {
        this.ID = null;
        this.coordinate = null;
        this.originalValue = null;
        this.effectiveValue = null;
        this.version = 0;
        this.dependsOn = null;
        this.influencingOn = null;
    }

    public CellDTO(String cellId,
                   CoordinateDTO coordinate,
                   String originalValue,
                   EffectiveValueDTO effectiveValue,
                   int version,
                   List<String> dependsOn,
                   List<String> influencingOn) {
        this.ID = cellId;
        this.coordinate = coordinate;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.version = version;
        this.dependsOn = dependsOn;
        this.influencingOn = influencingOn;
    }

    public String getId () { return ID; }

    public int getRow () {
        return coordinate.getRow();
    }

    public int getColumn () {
        return coordinate.getColumn();
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

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public List<String> getInfluencingOn() {
        return influencingOn;
    }

}
