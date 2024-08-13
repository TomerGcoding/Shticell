package cell.string;

import cell.Cell;

public class BooleanCell extends Cell {

    private final Boolean value;

    public BooleanCell(Boolean value) {this.value = value;}

    @Override
    public String toString() {return value? "TRUE" : "FALSE";}
    @Override
    public Boolean getOriginalValue() {return value;}
    @Override
    public String getEffectiveValue() {return toString();}


}
