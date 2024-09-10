package com.shticell.engine.range;

import com.shticell.engine.cell.api.Cell;

import java.util.List;

public interface Range {
    List<Cell> getCells();
    Double calculateSum();
    Double calculateAverage();
    boolean equals(Object o);

}
