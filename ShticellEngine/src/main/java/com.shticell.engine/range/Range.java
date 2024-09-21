package com.shticell.engine.range;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.sheet.api.Sheet;

import java.util.List;

public interface Range {
    boolean equals(Object o);
    List<EffectiveValue> getRangeValues(Sheet sheet);
    String getName();
    void addInfluence(String cellId);
    void removeInfluence(String cellId);
    List<String> getInfluenceOnCells();
    List<Cell> getCells(Sheet sheet);
}
