/**
 * 
 */
package com.jzb.excel;

import java.io.File;
import java.util.ArrayList;

import com.jzb.pdp.WikiParser;
import com.jzb.pdp.WikiParser.PrjData;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author PS00A501
 * 
 */
public class PdPExcel {

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
            PdPExcel me = new PdPExcel();
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
        String pdpFileName = "D:\\JZarzuela\\D_S_Escritorio\\pdp_test.xls";

        File fo = new File(pdpFileName);
        WritableWorkbook owb = Workbook.createWorkbook(fo);

        WikiParser wp = new WikiParser();
        ArrayList<PrjData> prjs = wp.parseData();

        new BKS_Prjs().createSheet(owb, prjs);
        new BKS_Desc().createSheet(owb, prjs);

        owb.write();
        owb.close();
    }

    public void doIt2(String[] args) throws Exception {
        String pdpFileName = "D:\\JZarzuela\\D_S_Escritorio\\libro2.xls";
        File fi = new File(pdpFileName);

        Workbook wb = Workbook.getWorkbook(fi);
        Sheet sheet = wb.getSheet("Hoja0");
        Cell label1 = sheet.getCell("F8");
        System.out.println(label1);
    }

}
