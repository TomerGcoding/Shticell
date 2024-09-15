package com.shticell.engine.range;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.sheet.api.Sheet;

import java.util.List;

public interface Range {
//    Double calculateSum(Sheet sheet);
//    Double calculateAverage(Sheet sheet);
    List<EffectiveValue> getRangeValues(Sheet sheet);
    String getName();
    void addInfluence(String cellId);
    void removeInfluence(String cellId);
    List<String> getInfluenceOnCells();
    List<Cell> generateCells(Sheet sheet);
}
