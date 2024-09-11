package com.shticell.engine.cell.api;

import com.shticell.engine.sheet.coordinate.Coordinate;


import java.util.List;

public interface Cell  {
    String getId ();
    Coordinate getCoordinate();
    String getOriginalValue();
    EffectiveValue getEffectiveValue();
    int getVersion();
    List<Cell> getDependsOn();
    List<Cell> getInfluencingOn();
    void setVersion(int currVersion);
    boolean calculateEffectiveValue();
    void addDependency(Cell referencedCell);
    void addInfluence(Cell thisCell);
    void removeFromInfluenceOn(Cell originCell);
    void deleteMeFromInfluenceList();
}