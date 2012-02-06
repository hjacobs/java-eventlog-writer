package de.zalando.orderengine.ruleengine

import jxl.Cell
import jxl.CellType
import jxl.Sheet
import jxl.Workbook


class ExcelGrabber {
    Workbook workbook;
    def openFile = { inputFile ->
        File inputWorkbook = new File(inputFile);
        try {
            workbook = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    def takeSheet = { sheetNo ->
        try {
            return workbook.getSheet(sheetNo);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    def cellValue = { sheet, cellName ->

        CellType type =  sheet.getCell(cellName).type

        def value = sheet.getCell(cellName).contents

        //                    println "set << ${cell} ${sheet.getCell(cell).contents} " + type
        if(type == CellType.EMPTY) {
            return ''
        }
        else if(type == CellType.LABEL) {
            return value.trim()
        }
        else if(type == CellType.NUMBER) {
            if("${value}".isInteger())
                return "${value}".asType(Integer.class)
            else if("${value}".isDouble())
                return "${value}".asType(Double.class)
            else if("${value}".isBigDecimal())
                return "${value}".asType(Integer.class)
            else if("${value}".isFloat())
                return "${value}".asType(Double.class)
        }
        else if(type == CellType.BOOLEAN) {
            return "${value}".asType(Boolean.class)
        }
        else if(type == CellType.ERROR) {
            return ''
        }
        else if(type == CellType.NUMBER_FORMULA) {
            return value
        }
        else if(type == CellType.DATE_FORMULA) {
            return value
        }
        else if(type == CellType.STRING_FORMULA) {
            return value
        }
        else if(type == CellType.BOOLEAN_FORMULA) {
            return value
        }
        else if(type == CellType.FORMULA_ERROR) {
            return value
        }
        else if(type == CellType.DATE) {
            return value
        }
        else
            return value
    }

    void read() {

        Sheet sheet = workbook.getSheet(0);
        // Loop over first 10 column and lines

        for (int j = 0; j < sheet.getColumns(); j++) {
            for (int i = 0; i < sheet.getRows(); i++) {
                Cell cell = sheet.getCell(j, i);
                CellType type = cell.getType();
                if (cell.getType() == CellType.LABEL) {
                    System.out.println("I got a label "
                            + cell.getContents());
                }

                if (cell.getType() == CellType.NUMBER) {
                    System.out.println("I got a number "
                            + cell.getContents());
                }

            }
        }
    }

    void close() {
        try{
            workbook.close()
        } catch (Exception e) {
            return null;
        }
    }

    //    def

}