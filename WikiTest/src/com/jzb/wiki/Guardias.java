/**
 * 
 */
package com.jzb.wiki;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;
import jxl.Workbook;

import com.jzb.util.Des3Encrypter;
import com.jzb.wiki.util.BKSWikiHelper;

/**
 * @author n000013
 * 
 */
public class Guardias {

    private static final SimpleDateFormat s_df = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            Guardias me = new Guardias();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        System.out.println("*** Reading info from excel file");
        Sheet sheet = _openExcelSheet();
        int monthRow = _findMothRow(sheet, 3, _getFirstMonday());
        int maxCol = _findMaxCol(sheet, monthRow);

        System.out.println("*** Generating wiki text from excel info");
        String text = _generateWikiText(sheet, monthRow, maxCol);
        System.out.println(text);

        BKSWikiHelper helper = new BKSWikiHelper();
        helper.login(Des3Encrypter.decryptStr("PjN1Jb0t6CY0Eo9zcFVohw=="), Des3Encrypter.decryptStr("PjN1Jb0t6CYD25gJXVCyxw=="));
        helper.navigateTo("?title=Guardias_BKS_Core&action=edit", "Editando Guardias BKS Core - BanksphereWiki");
        helper.changeEditingText(text, "Cambio automático desde excel", false, "Guardias BKS Core - BanksphereWiki");
    }

    private int _findMaxCol(Sheet sheet, int monthRow) throws Exception {
        int maxCol = 2;
        for (;;) {
            maxCol++;
            if (maxCol >= sheet.getColumns())
                break;
            boolean isDate = sheet.getCell(maxCol, monthRow).getType().equals(CellType.DATE);
            if (!isDate)
                break;
        }
        return maxCol;
    }

    private String _generateWikiText(Sheet sheet, int monthRow, int maxCol) throws Exception {

        StringBuffer sb = new StringBuffer();

        sb.append("La siguiente tabla muestra las guardias de \'\'\'BKS Core\'\'\' y las personas asignadas:\n\n\n");

        sb.append("{| cellpadding=\"4\" cellspacing=\"0\" width=100% style=\"border:1px solid #A0A0A0\" \n");
        for (int i = 0; i==0 || sheet.getCell(1, monthRow + i ).getContents().length() > 0; i++) {
            sb.append("|- \n");
            for (int col = 1; col < maxCol; col++) {
                Cell cell = sheet.getCell(col, monthRow + i);
                sb.append("  {{GBC_CELL");
                sb.append(((col == 1) || (i == 0)) ? "_T":"");
                sb.append(" | ");
                sb.append(_getCellValue(cell));
                sb.append(" | }}\n");
            }
            sb.append("\n");
        }
        sb.append("|}");

        return sb.toString();
    }

    private String _getCellValue(Cell cell) throws Exception {
        String content;
        if (cell.getType() == CellType.DATE) {
            content = s_df.format(((DateCell) cell).getDate());
        } else {
            content = cell.getContents();
        }
        return content;
    }

    private int _findMothRow(Sheet sheet, int col, String value) throws Exception {

        for (int row = 0; row < sheet.getRows(); row++) {
            Cell cell = sheet.getCell(col, row);
            if (value.equals(_getCellValue(cell)))
                return row;
        }

        throw new Exception("Excel sheet doesn't have a cell labeled as: " + value);
    }

    private Sheet _openExcelSheet() throws Exception {
        String excelFileName = "C:\\JZarzuela\\DOCs\\BankSphere\\Guardias.xls";
        File fi = new File(excelFileName);
        if (!fi.exists()) {
            throw new Exception("Excel file doesn't exist:" + fi.getAbsolutePath());
        }

        System.setProperty("jxl.nopropertysets", "true");
        Workbook wb = Workbook.getWorkbook(fi);
        Sheet sheet = wb.getSheet(0);
        return sheet;
    }

    private String _getFirstMonday() {

        int dayOffset[] = { -1, 1, 0, 6, 5, 4, 3, 2 };

        Calendar today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_MONTH, 1);
        today.add(Calendar.DAY_OF_MONTH, dayOffset[today.get(Calendar.DAY_OF_WEEK)]);
        return s_df.format(today.getTime());
    }
}
