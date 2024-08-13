package cell;

import java.util.List;

public abstract class Cell {

    private static int idGenerator = 0;
    private int id;
    private int lastUpdate;
    private List<Cell> dependency;
    public List<Cell> influense;
    abstract public Object getOriginalValue();
    abstract public Object getEffectiveValue();


}
