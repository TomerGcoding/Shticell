package com.shticell.ui.utils;

import java.util.Map;

public class TablePrinter {
    public static void printVersionToCellChangedTable(Map<Integer, Integer> versionToCellChangedTable) {
        System.out.println("+------------+------------------------+");
        System.out.println("| Version    | Number of Cells Changed |");
        System.out.println("+------------+------------------------+");

        for (Map.Entry<Integer, Integer> entry : versionToCellChangedTable.entrySet()) {
            int version = entry.getKey();
            int cellsChanged = entry.getValue();

            System.out.printf("| %-10d | %-22d |\n", version, cellsChanged);
        }

        System.out.println("+------------+------------------------+");
    }
}
