package dto;


import java.io.Serializable;

public class EffectiveValueDTO implements Serializable {

    private String value;

    public EffectiveValueDTO() {
    }

    public EffectiveValueDTO(String value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value.toUpperCase().equals("TRUE") || value.toUpperCase().equals("FALSE")) return value.toUpperCase();
        else return value.toString();
    }
}
