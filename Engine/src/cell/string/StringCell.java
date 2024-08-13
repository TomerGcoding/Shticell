package cell.string;

import cell.Cell;

public class StringCell extends Cell {

    private final String value;

    public StringCell(String value) {this.value = value;}

    @Override
    public String toString() {return value;}
    @Override
    public String getOriginalValue() {return value;}
    @Override
    public String getEffectiveValue() {return value;}


}

