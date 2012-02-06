package de.zalando.orderengine.ruleengine

import jxl.Sheet




class ExcelBackedRulesetHolder extends HashMap<String, String>{

    final def extensions = ''<<'' // only for error reporting
    final def sheetsCache = [:]  // cached sheets
    final def xlsFilePath
    final def xlsGrabber

    ExcelBackedRulesetHolder(xlsFilePath) {
        this.xlsFilePath = xlsFilePath
        xlsGrabber = new ExcelGrabber();
        if(!xlsGrabber.openFile(xlsFilePath)) {
            throw new RuntimeException("File was not opened: ${xlsFilePath}")
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        close()
                    }
                });
    }

    ExcelBackedRulesetHolder(xlsFilePath, sheetNo) {
        this(xlsFilePath)
        readRulesFromSheet(sheetNo)
    }

    def readRulesFromSheet(sheetNo){
        def emptyRowCounter = 0
        def row = 0
        while(++row && emptyRowCounter < 3) {
            def cellA = readCell(sheetNo, "A${row}")
            if(cellA) {
                def cellB = readCell(sheetNo, "B${row}")
                if(cellB) {
                    put(cellA, cellB)
                    emptyRowCounter = 0
                }
                else {
                    emptyRowCounter++;
                }
            }
            else {
                emptyRowCounter++;
            }
        }
        return this
    }

    private def takeSheet(sheetNo) {
        Sheet sheet = sheetsCache[sheetNo]

        if(!sheet) {
            sheet = xlsGrabber.takeSheet(sheetNo)
            if(sheet) {
                sheetsCache[sheetNo] = sheet
            }
        }
        return sheet
    }

    @Override
    def put(methodName, code) {
        super.put(methodName, "${code}" )
        extensions << "def ${methodName}() { \n\t${code} \n}\n\n"
    }

    def readCell (sheetNo, cell){
        try{
            def sheet = takeSheet(sheetNo)
            if(sheet)
                return xlsGrabber.cellValue(sheet, cell)
            //            return ("${sheet.getCell(cell).contents}").trim()
            else {
                println "No sheet no ${sheetNo} in ${xlsFilePath}."
                return null
            }
        } catch(Throwable t) {
            //            println "Error reading sheet ${sheetNo}.${cell}: ${t.getMessage()}"
            return null
        }
    }

    def close() {
        try{
            xlsGrabber.close()
        }
        catch(Throwable t){
        }
        //        println "... terminated."
    }
}