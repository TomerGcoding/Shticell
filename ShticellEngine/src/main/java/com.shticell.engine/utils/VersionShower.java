package com.shticell.engine.utils;

import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VersionShower {


    public static Map<Integer,Integer> getVersionsToChooseFrom(Collection<SheetDTO> sheets) {
        Map<Integer,Integer> versions = new HashMap<>();
        for (SheetDTO sheet : sheets) {
            versions.put(sheet.getCurrVersion(),countNumberOfCellsChangedInVersion(sheet,sheet.getCurrVersion()));
        }
        return versions;
    }

    private static Integer countNumberOfCellsChangedInVersion(SheetDTO sheet, int currVersion) {
        int numberOfCellsChanged = 0;
        for (CellDTO cell : sheet.getActiveCells().values()){
            if(cell.getVersion()==currVersion){
                numberOfCellsChanged++;
            }
        }
        return numberOfCellsChanged;
    }

}

