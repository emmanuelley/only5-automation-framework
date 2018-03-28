package com.only5.automation_framework.utils;

import com.only5.automation_framework.base.Res;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Rajesh
 */
public class Excel {

    public final ExcelContext context;
    private int rowOffset;
    private int columnOffset;

    public Excel(ExcelContext context) {
        this.context = context;
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public void setRowOffset(int rowOffset) {
        this.rowOffset = rowOffset;
    }

    public int getColumnOffset() {
        return columnOffset;

    }

    public String[][] getData(String... columnNames) {
        return getExcelRecords(Res.getResource(((ExcelContext) context).getExcelFile()), columnNames);
    }

    public String getSheetName() {
        return ((ExcelContext) context).getSheetName();
    }

    private String[][] getExcelRecords(URL resource, String[] columnNames) {
        List<String[]> neededRows = new ArrayList<>();
        //JOptionPane.showInputDialog(resource);
        XSSFWorkbook workbook = getWorkbook(resource);
        XSSFSheet sheet = null;
        if (getSheetName() == null || getSheetName().isEmpty()) {
            sheet = workbook.getSheetAt(0);
        } else {
            sheet = workbook.getSheet(getSheetName());
        }
        setRowOffset(sheet.getFirstRowNum());
        setColumnOffset(sheet.getRow(getRowOffset()));
        List<String> columnHeaders = getColumnHeaders(sheet);
        List<Integer> neededIndexes = new ArrayList<>();

        if (columnNames == null || columnNames.length == 0) {
            for (int i = 0; i < columnHeaders.size(); ++i) {
                neededIndexes.add(getColumnOffset() + i);
            }
        } else {
            for (String neededName : columnNames) {
                int neededIndex = columnHeaders.indexOf(neededName);
                if (neededIndex < 0) {
                    throw new RuntimeException("Column Name: \"" + neededName + "\" was not found in the excel");
                }
                neededIndexes.add(getColumnOffset() + Integer.valueOf(neededIndex));
            }
        }

        int numNeededCols = neededIndexes.size();
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();
            if (row.getRowNum() <= sheet.getFirstRowNum()) {
            } else {
                Iterator<Cell> cellIterator = row.cellIterator();
                String[] neededRow = new String[numNeededCols];
                int rowCnt = 0;
                while (cellIterator.hasNext()) {
                    XSSFCell cell = (XSSFCell) cellIterator.next();
                    for (int i = 0; i < numNeededCols; i++) {
                        if (cell.getColumnIndex() == neededIndexes.get(i)) {
                            String cellValue = "";
                            switch (cell.getCellType()) {
                                case Cell.CELL_TYPE_BOOLEAN:
                                    cellValue = Boolean.toString(cell.getBooleanCellValue());
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    cellValue = Double.toString(cell.getNumericCellValue());
                                    break;
                                case Cell.CELL_TYPE_STRING:
                                    cellValue = cell.getStringCellValue();
                                    if (cellValue.trim().equalsIgnoreCase("<<blank>>")) {
                                        cellValue = "";
                                    }
                                    break;
                            }

                            if (cellValue.isEmpty() || cellValue == null) {
                            } else {
                                neededRow[rowCnt] = cellValue;
                            }
                            rowCnt++;
                        }
                    }
                }

                neededRows.add(neededRow);
            }
        }
        return neededRows.toArray(new String[neededRows.size()][]);
    }

    private List<String> getColumnHeaders(XSSFSheet sheet) {
        List<String> columnHeaders = new ArrayList<>();
        XSSFRow header = sheet.getRow(getRowOffset());
        Iterator<Cell> cellIterator = header.cellIterator();
        while (cellIterator.hasNext()) {
            XSSFCell cell = (XSSFCell) cellIterator.next();
            String headerVal = "";
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    headerVal = Boolean.toString(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    headerVal = Double.toString(cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    headerVal = cell.getStringCellValue();
                    break;
            }
            if (headerVal.isEmpty() || headerVal == null) {
            } else {
                columnHeaders.add(headerVal);
            }
        }
        return columnHeaders;
    }

    private void setColumnOffset(XSSFRow row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        XSSFCell cell = (XSSFCell) cellIterator.next();
        this.columnOffset = cell.getColumnIndex();
    }

    public XSSFWorkbook getWorkbook(URL resource) {
        XSSFWorkbook workbook = null;
        try {
            File file = new File(resource.toURI());
            FileInputStream fis = new FileInputStream(file);
            try {
                workbook = new XSSFWorkbook(fis);
            } catch (IOException ex) {
                throw new RuntimeException(
                        "Unable to open the file. Either the file is no present or you are pointing to an incorrect file url");
            }
        } catch (URISyntaxException | FileNotFoundException ex) {
            throw new RuntimeException("Unable to read the file or the url is incorrect");
        }
        return workbook;
    }

    public String getColumnDataByRowIndex(int rowIndex, String... columnName) {
        String[][] columnData = getExcelRecords(Res.getResource(((ExcelContext) context).getExcelFile()), columnName);
        return columnData[rowIndex][0];
    }
}