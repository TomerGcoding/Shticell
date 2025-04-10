package com.shticell.engine.utils;
import dto.SheetDTO;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import com.shticell.engine.sheet.impl.SheetImpl;
import jaxb.schema.generated.STLCell;
import jaxb.schema.generated.STLRange;
import jaxb.schema.generated.STLSheet;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class SheetLoader implements Serializable {

    private static final String JAXB_XML_GENERATED_PACKAGE = "jaxb.schema.generated";

    private static final int MAX_ROWS = 50;
    private static final int MIN_ROWS = 1;
    private static final int MAX_COLUMNS = 20;
    private static final int MIN_COLUMNS = 1;

    private Sheet sheet;
    private final Map<Integer, SheetDTO> versions = new HashMap<>();
    public void loadSheetFile(String filePath,String userName) throws JAXBException {
        validateFilePath(filePath);

        File file = new File(filePath);
        validateFileExistence(file);

        STLSheet stlSheet = unmarshalSheet(file);
        convertStlSheetToOurSheet(stlSheet,userName);
    }

    private void validateFilePath(String filePath) throws JAXBException {
        if (!filePath.toLowerCase().endsWith(".xml")) {
            throw new JAXBException(filePath + " is not an XML file");
        }
    }

    private void validateFileExistence(File file) throws JAXBException {
        if (!file.exists()) {
            throw new JAXBException(file.getPath() + " does not exist");
        }
    }

    private STLSheet unmarshalSheet(File file) throws JAXBException {
        try {
            JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GENERATED_PACKAGE);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            return (STLSheet) unmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new JAXBException("Failed to unmarshal XML file: " + file.getPath(), e);
        }
    }

    private void convertStlSheetToOurSheet(STLSheet stlSheet,String userName) {
        String sheetName = stlSheet.getName();
        int columns = stlSheet.getSTLLayout().getColumns();
        int rows = stlSheet.getSTLLayout().getRows();

        validateSheetDimensions(rows, columns);

        int columnWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        int rowHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        sheet = new SheetImpl(sheetName, rows, columns, rowHeight, columnWidth);

        populateSheetWithRanges(stlSheet,rows,columns);
        populateSheetWithCells(stlSheet, rows, columns,userName);
    }

    private void validateSheetDimensions(int rows, int columns) {
        if (rows > MAX_ROWS || rows < MIN_ROWS) {
            throw new IllegalArgumentException("Sheet size must be between " + MIN_ROWS + " and " + MAX_ROWS + " rows");
        }
        if (columns > MAX_COLUMNS || columns < MIN_COLUMNS) {
            throw new IllegalArgumentException("Sheet size must be between " + MIN_COLUMNS + " and " + MAX_COLUMNS + " columns");
        }
    }

    private void populateSheetWithRanges(STLSheet stlSheet, int rows, int columns) {
        if (stlSheet.getSTLRanges()!= null) {
            for (STLRange range : stlSheet.getSTLRanges().getSTLRange()) {
                String startCellId = range.getSTLBoundaries().getFrom();
                String endCellId = range.getSTLBoundaries().getTo();
                int[] startCellNumericCoord = CoordinateFormatter.cellIdToIndex(startCellId);
                validateCellCoordinates(startCellNumericCoord[0], startCellNumericCoord[1], rows, columns);
                int[] endCellNumericCoord = CoordinateFormatter.cellIdToIndex(endCellId);
                validateCellCoordinates(endCellNumericCoord[0], endCellNumericCoord[1], rows, columns);
                this.sheet.addRange(range.getName(), startCellId + ".." + endCellId);
            }
        }
    }

    private void populateSheetWithCells(STLSheet stlSheet, int rows, int columns,String userName) {
        if ((stlSheet.getSTLCells()!=null)) {
            for (STLCell cell : stlSheet.getSTLCells().getSTLCell()) {
                int row = cell.getRow() - 1;
                int column = CoordinateFormatter.getColumnIndex(cell.getColumn());

                validateCellCoordinates(row, column, rows, columns);

                String value = cell.getSTLOriginalValue();
                this.sheet = sheet.setCell(row, column, value,userName);
            }
            sheet.incrementVersion();
        }
    }

    private void validateCellCoordinates(int row, int column, int rows, int columns) {
        if (row >= rows || row < 0 || column >= columns || column < 0) {
            throw new IllegalArgumentException(CoordinateFormatter.indexToCellId(row, column) + " is not a valid cell");
        }
    }

    public Sheet getSheet() {
        return this.sheet;
    }

}

