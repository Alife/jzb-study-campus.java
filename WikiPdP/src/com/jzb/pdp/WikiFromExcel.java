/**
 * 
 */
package com.jzb.pdp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * @author PS00A501
 * 
 */
public class WikiFromExcel {

    public static class PrjData {

        public String desc       = "";
        public String JP         = "";
        public String PdPMainPrj = null;
        public String title      = "";
        public String wikireq    = "";

        public String toString() {
            return toString2("");
        }

        public String toXML() {
            return toXML2("");
        }

        /**
         * @see java.lang.Object#toString()
         */
        private String toString2(String padding) {
            String s = "";
            s += padding + "PdPMainPrj = " + PdPMainPrj + "\n";
            s += padding + "title = " + title + "\n";
            s += padding + "JP = " + JP + "\n";
            s += padding + "wikireq = " + wikireq + "\n";
            s += padding + "desc = " + desc + "\n";
            return s;
        }

        private String toXML2(String padding) {
            String s = "";
            s += padding + "<prj>\n";
            s += padding + "  <title>" + title + "</title>\n";
            s += padding + "  <JP>" + JP + "</JP>\n";
            s += padding + "  <wikireq>" + wikireq + "</wikireq>\n";
            s += padding + "  <desc><![CDATA[" + desc + "]]><desc>\n";
            s += padding + "</prj>\n";
            return s;
        }

    }

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
            WikiFromExcel me = new WikiFromExcel();
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

        ArrayList<PrjData> prjs;

        String pdpFileName = "C:\\Documents and Settings\\n000013\\Desktop\\pdp_test.xls";
        File fi = new File(pdpFileName);
        Workbook wb = Workbook.getWorkbook(fi);

        System.out.println("__TOC__\n\n");

        System.out.println("== Proyectos BKS Core ==\n<br>\n");
        prjs = readPrjs(wb, 0);
        printWikiPdPTable(prjs);

        System.out.println("\n\n== Proyectos BKS Seguridad ==\n<br>\n");
        prjs = readPrjs(wb, 2);
        printWikiPdPTable(prjs);

        prjs = readPrjs(wb, 4);
        if (prjs.size() > 0) {
            System.out.println("\n\n== Proyectos BKS Obsoletos ==\n<br>\n");
            printWikiPdPTable(prjs);
        }
        wb.close();

    }

    // -------------------------------------------------------------------------------
    private void adjustData(PrjData prj) throws Exception {

        int pos = prj.title.indexOf('\n');
        if (pos > 0) {
            prj.PdPMainPrj = prj.title.substring(0, pos);
            while (Character.isWhitespace(prj.title.charAt(pos))) {
                pos++;
            }
            prj.title = prj.title.substring(pos);
        }
        if (prj.PdPMainPrj == null)
            prj.PdPMainPrj = "";
    }

    // -------------------------------------------------------------------------------
    private void printWikiPdPTable(ArrayList<PrjData> list) throws Exception {

        for (PrjData i : list) {
            String aTitle = (i.PdPMainPrj.equals("") ? i.title : i.PdPMainPrj + " - " + i.title);
            if (i.wikireq != null && !i.wikireq.equals("")) {
                aTitle = "[" + i.wikireq + " " + aTitle + "]";
            }

            System.out.println("{{ITEM_PDP_PRJ");
            System.out.println("| Titulo = " + aTitle);
            System.out.println("| JP = " + i.JP);
            System.out.println("| Descripcion =");
            System.out.println(i.desc);
            System.out.println("}}");
            System.out.println("");
            System.out.println("");
        }
    }

    // -----------------------------------------------------------------------------
    private ArrayList<PrjData> readPrjs(Workbook wb, int index) throws Exception {

        ArrayList<PrjData> prjs = new ArrayList<PrjData>();

        Sheet sheet1 = wb.getSheet(index);
        int iRow1 = 1 + sheet1.findLabelCell("Proyecto").getRow();
        int iTitulo = sheet1.findLabelCell("Proyecto").getColumn();
        int iJP = sheet1.findLabelCell("JP").getColumn();
        int iWRQ = sheet1.findLabelCell("Link WikiReq").getColumn();

        Sheet sheet2 = wb.getSheet(index + 1);
        int iRow2 = -iRow1 + 1 + sheet2.findLabelCell("Descripción").getRow();
        int iDescripcion = sheet2.findLabelCell("Descripción").getColumn();

        for (int n = iRow1; n < sheet1.getRows(); n++) {
            PrjData prj = new PrjData();
            prj.title = sheet1.getCell(iTitulo, n).getContents();
            prj.JP = sheet1.getCell(iJP, n).getContents();
            prj.wikireq = sheet1.getCell(iWRQ, n).getContents();
            prj.desc = sheet2.getCell(iDescripcion, n + iRow2).getContents();
            adjustData(prj);
            prjs.add(prj);
        }

        return prjs;
    }

}