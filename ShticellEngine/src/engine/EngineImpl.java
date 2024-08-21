package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.dto.VersionTableDTO;
import engine.sheet.api.Sheet;
import engine.sheet.coordinate.CoordinateFormatter;
import engine.sheet.impl.SheetImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jaxb.schema.generated.STLCell;
import jaxb.schema.generated.STLSheet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements Engine{
    private static final String JAXB_XML_GENERATED_PACKAGE = "jaxb.schema.generated";
    private Sheet sheet = null;
    private List<SheetDTO> sheets = new ArrayList<>();

    public EngineImpl() {}
    public EngineImpl(Sheet sheet) {this.sheet = sheet;}
    @Override
    public void loadSheetFile(String filePath) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GENERATED_PACKAGE);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        File file = new File(filePath);
        STLSheet stlSheet = (STLSheet) unmarshaller.unmarshal(file);
        this.stlSheetToOurSheet(stlSheet);
    }

    private void stlSheetToOurSheet(STLSheet stlSheet) {
        String sheetName = stlSheet.getName();
        int rows = stlSheet.getSTLLayout().getRows();
        int columns = stlSheet.getSTLLayout().getColumns();
        int columnWidth = stlSheet.getSTLLayout().getSTLSize().getColumnWidthUnits();
        int rowHeight = stlSheet.getSTLLayout().getSTLSize().getRowsHeightUnits();
        sheet = new SheetImpl(sheetName, rows, columns, columnWidth, rowHeight);
        for(STLCell cell : stlSheet.getSTLCells().getSTLCell()){
            sheet.setCell(cell.getRow()-1, CoordinateFormatter.getColumnIndex(cell.getColumn()), cell.getSTLOriginalValue());
        }
    }

    @Override
    public SheetDTO showSheet() {
        SheetDTO sheetDTO = new SheetDTO(sheet.getCells(),sheet.getVersion(),
                sheet.getSheetName(),sheet.getProperties());
        return sheetDTO;
    }

    @Override
    public CellDTO getCellInfo(String cellId) {

    }

    @Override
    public void setCell(String cellId, String cellValue) {

        //just an example of some kind of exception struct
        try {
            sheet.setCell(cellId, cellValue);
            }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to update cell: " + cellId + " with the value: " + cellValue + "Please try again");
        }
        SheetDTO newSheet = new SheetDTO(sheet.getCells(),sheet.getVersion(), sheet.getSheetName(),sheet.getProperties());
        sheet.incrementVersion();
        sheets.add(newSheet);
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
