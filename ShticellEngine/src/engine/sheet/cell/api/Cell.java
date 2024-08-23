package engine.sheet.cell.api;

//import engine.sheet.api.Sheet;
import engine.sheet.coordinate.Coordinate;

//import java.io.Serializable;
import java.util.List;

public interface Cell  {
    String getId ();
    Coordinate getCoordinate();
    String getOriginalValue();
    void setCellOriginalValue(String value);
    EffectiveValue getEffectiveValue();
  //  void calculateEffectiveValue(Sheet sheet);
    int getVersion();
    List<Cell> getDependsOn();
    List<Cell> getInfluencingOn();
    void setVersion(int currVersion);
    void deleteCell();
    void deleteDependency(Cell deleteMe);
    boolean calculateEffectiveValue();
}