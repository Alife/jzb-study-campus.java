/**
 * 
 */
package com.jzb.pdp;

import java.io.File;
import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * @author PS00A501
 * 
 */
public class CopyOfWikiFromExcel {

    public static class PrjData {

        public String PdPMainPrj = null;
        public String title      = "";
        public String JP         = "";
        public String wikireq    = "";
        public String desc       = "";

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

        public String toString() {
            return toString2("");
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

        public String toXML() {
            return toXML2("");
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
            CopyOfWikiFromExcel me = new CopyOfWikiFromExcel();
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
        File fi = new File(pdpFileName);

        Workbook wb = Workbook.getWorkbook(fi);
        ArrayList<PrjData> prjs = readPrjs(wb, 0);
        wb.close();

        printWikiText_FMT4(prjs);

    }

    // -----------------------------------------------------------------------------
    private ArrayList<PrjData> readPrjs(Workbook wb, int index) throws Exception {

        ArrayList<PrjData> prjs = new ArrayList<PrjData>();

        Sheet sheet1 = wb.getSheet(index);
        int iRow1 = 1 + sheet1.findLabelCell("Proyecto").getRow();
        int iTitulo = sheet1.findLabelCell("Proyecto").getColumn();
        int iJP = sheet1.findLabelCell("JP").getColumn();

        Sheet sheet2 = wb.getSheet(index + 1);
        int iRow2 = -iRow1 + 1 + sheet2.findLabelCell("Descripcion").getRow();
        int iDescripcion = sheet2.findLabelCell("Descripcion").getColumn();

        for (int n = iRow1; n < sheet1.getRows(); n++) {
            PrjData prj = new PrjData();
            prj.title = sheet1.getCell(iTitulo, n).getContents();
            prj.JP = sheet1.getCell(iJP, n).getContents();
            prj.desc = sheet2.getCell(iDescripcion, n + iRow2).getContents();

            adjustData(prj);
            prjs.add(prj);
        }

        return prjs;
    }

    // -------------------------------------------------------------------------------
    private void adjustData(PrjData prj) throws Exception {

        int pos = prj.title.indexOf('\n');
        if (pos < 0)
            return;

        prj.PdPMainPrj = prj.title.substring(0, pos);
        while (Character.isWhitespace(prj.title.charAt(pos))) {
            pos++;
        }
        prj.title = prj.title.substring(pos);
    }

    // -------------------------------------------------------------------------------
    private void printWikiText_FMT0(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");
        System.out.println("{| cellpadding=\"4\" cellspacing=\"0\" style=\"border:1px solid #A0A0A0\"");
        System.out.println("|- style=\"text-align:center; background:#E0E0E0\"");
        System.out.println("| '''Nombre''' || '''Descripción'''");

        for (PrjData i : list) {
            System.out.println("\n\n|- valign=\"center\" style=\"text-align:left;\"");
            System.out.println("| style=\"border-top:1px solid #A0A0A0\" |");
            if (i.PdPMainPrj == null) {
                System.out.println("<h3>" + i.title + "</h3>");
            } else {
                System.out.println(i.PdPMainPrj);
                System.out.println(": <h3>" + i.title + "</h3>");
            }
            System.out.println(": JP: " + i.JP);
            System.out.println("| style=\"border-top:1px solid #A0A0A0; border-left:1px solid #A0A0A0;\" |");
            System.out.println(i.desc);
        }

        System.out.println("|}");
    }

    // -------------------------------------------------------------------------------
    private void printWikiText_FMT1(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");
        System.out.println("{| cellpadding=\"4\" cellspacing=\"0\" style=\"border:1px solid #A0A0A0\"");
        System.out.println("|- style=\"text-align:center; background:#E0E0E0\"");
        System.out.println("| '''Nombre''' || '''Descripción'''");

        for (PrjData i : list) {

            System.out.println("{{FMT1_PRJ_PDP1");
            System.out.println("| Titulo = " + i.title);
            System.out.println("| JP = " + i.JP);
            System.out.println("| Descripcion = \n" + i.desc);
            System.out.println("}}");
        }

        System.out.println("|}");
    }

    // -------------------------------------------------------------------------------
    private void printWikiText_FMT2(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");

        for (PrjData i : list) {

            System.out.println("{{FMT2_PRJ_PDP1");
            System.out.println("| Titulo = " + i.title);
            System.out.println("| JP = " + i.JP);
            System.out.println("| Descripcion = \n" + i.desc);
            System.out.println("}}\n\n");
        }
    }

    // -------------------------------------------------------------------------------
    private void printWikiText_FMT3(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");

        for (PrjData i : list) {

            System.out.println("{{FMT3_PRJ_PDP1");
            System.out.println("| Titulo = " + i.title);
            System.out.println("| JP = " + i.JP);
            System.out.println("| Descripcion = \n" + i.desc);
            System.out.println("}}\n\n");
        }
    }

    // -------------------------------------------------------------------------------
    private void printWikiText_FMT4(ArrayList<PrjData> list) throws Exception {
        System.out.println("__TOC__\n\n");

        for (PrjData i : list) {
            System.out.println("{{FMT4_PRJ_PDP1");
            System.out.println("| Titulo = " + (i.PdPMainPrj == null ? i.title : i.PdPMainPrj + " - " + i.title));
            System.out.println("| JP = " + i.JP);
            System.out.println("| Descripcion =");
            System.out.println(i.desc);
            System.out.println("}}");
            System.out.println("");
            System.out.println("");
        }
    }

}