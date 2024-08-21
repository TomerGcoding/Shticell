package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.dto.VersionTableDTO;
import engine.sheet.api.Sheet;
import engine.sheet.coordinate.CoordinateFormatter;
import engine.sheet.impl.SheetImpl;
import engine.utils.SheetLoader;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jaxb.schema.generated.STLCell;
import jaxb.schema.generated.STLSheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements Engine{
    private Sheet sheet = null;
    private SheetLoader sheetLoader = new SheetLoader();
    private List<SheetDTO> sheets = new ArrayList<>();

    @Override
    public void loadSheetFile(String filePath) throws JAXBException {
        try {
            sheetLoader.loadSheetFile(filePath);
            this.sheet = sheetLoader.getSheet();
        } catch (JAXBException | IllegalArgumentException e) {
            // Handle exceptions, such as invalid XML format or invalid sheet dimensions
            throw e;  // rethrow to maintain behavior
        }
    }

    @Override
    public SheetDTO showSheet() {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        SheetDTO sheetDTO = new SheetDTO(sheet.getCells(),sheet.getVersion(),
                sheet.getSheetName(),sheet.getProperties());
        return sheetDTO;
    }

    @Override
    public CellDTO showCell() {
        return null;
    }

    @Override
    public SheetDTO setCell(String cellId, String cellValue) {

        //just an example of some kind of exception struct
        try {
            sheet.setCell(cellId, cellValue);
            }
        catch (Exception e) {
            System.out.println("something went wrong with update the cell, please try again");
            return null;
        }
        finally {
            System.out.println("we tried to update cell: " + cellId + "with value: " + cellValue);
        }
        SheetDTO newSheet = new SheetDTO(sheet.getCells(),sheet.getVersion(), sheet.getSheetName(),sheet.getProperties());
        sheets.add(newSheet);
        return newSheet;
    }

    @Override
    public VersionTableDTO showVersionTable() {
        return null;
    }

    @Override
    public SheetDTO showChosenVersion(int chosenVersion) {
        return null;
    }
}
