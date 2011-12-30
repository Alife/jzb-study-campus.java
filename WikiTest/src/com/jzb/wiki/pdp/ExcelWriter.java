/**
 * 
 */
package com.jzb.wiki.pdp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;


import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author n000013
 * 
 */
public class ExcelWriter {

    public static void writeInfoToExcel(TreeMap<String, ArrayList<IPdPItem>> releases) throws Exception {

        System.setProperty("jxl.nopropertysets", "true");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

        String excelFileName = "C:\\pdp_info-" + sdf.format(new Date()) + ".xls";
        File fi = new File(excelFileName);

        System.out.println("Creating Excel file: '" + excelFileName + "'");
        WritableWorkbook wwb = Workbook.createWorkbook(fi);

        for (Map.Entry<String, ArrayList<IPdPItem>> entry : releases.entrySet()) {

            String name = entry.getKey().replace('_',' ');
            int p1 = name.indexOf("Prjs");
            if (p1 > 0) {
                name = name.substring(p1 + 5);
            }
            name = name.trim();
            WritableSheet wsheet = wwb.createSheet(name, 10000);
            _fillSheet(wsheet, entry.getValue());
        }

        wwb.write();
        wwb.close();
    }

    private static void _fillItemType_Project(WritableSheet wsheet, IPdPItem prj) throws Exception {

        int curRow=wsheet.getRows();

        WritableCellFormat wcf1 = new WritableCellFormat();
        // wcf1.setBackground(Colour.YELLOW);
        wcf1.setAlignment(Alignment.JUSTIFY);
        wcf1.setVerticalAlignment(VerticalAlignment.TOP);
        WritableFont wf1 = new WritableFont(WritableFont.ARIAL, 10);
        wf1.setColour(Colour.GRAY_50);
        wf1.setBoldStyle(WritableFont.BOLD);
        wcf1.setFont(wf1);
        wcf1.setBorder(Border.ALL, BorderLineStyle.THIN);
        wcf1.setWrap(true);

        wsheet.addCell(new Label(1, curRow, prj.getAttrVal(0), wcf1));
        wsheet.addCell(new Label(2, curRow, prj.getAttrVal(1), wcf1));
        wsheet.addCell(_getNumberCellFromValue(3, curRow, prj.getAttrVal(2), wcf1));
        wsheet.addCell(_getNumberCellFromValue(4, curRow, prj.getAttrVal(3), wcf1));
        wsheet.addCell(new Label(5, curRow, prj.getAttrVal(4), wcf1));
        wsheet.addCell(new Label(6, curRow, prj.getAttrVal(5), wcf1));
    }

    private static WritableCell _getNumberCellFromValue(int c, int r, String value, WritableCellFormat wcf) {
        WritableCell wc;
        try {
            wc = new Number(c, r, Double.parseDouble(value), wcf);
        } catch (Exception e) {
            wc = new Label(c, r, value, wcf);
        }
        return wc;
    }

    private static void _fillItemType_Title(WritableSheet wsheet, IPdPItem title) throws Exception {

        int curRow=wsheet.getRows();

        WritableCellFormat wcf1 = new WritableCellFormat();
        wcf1.setBackground(Colour.YELLOW2);
        wcf1.setAlignment(Alignment.JUSTIFY);
        wcf1.setVerticalAlignment(VerticalAlignment.TOP);
        WritableFont wf1 = new WritableFont(WritableFont.ARIAL, 10);
        wf1.setColour(Colour.BLACK);
        wf1.setBoldStyle(WritableFont.BOLD);
        wcf1.setFont(wf1);
        wcf1.setBorder(Border.ALL, BorderLineStyle.THICK);

        WritableCellFormat wcf2 = new WritableCellFormat();
        wcf2.setBackground(Colour.GRAY_25);
        wcf2.setAlignment(Alignment.CENTRE);
        wcf2.setVerticalAlignment(VerticalAlignment.TOP);
        WritableFont wf2 = new WritableFont(WritableFont.ARIAL, 10);
        wf2.setColour(Colour.BLACK);
        wf2.setBoldStyle(WritableFont.BOLD);
        wcf2.setFont(wf2);
        wcf2.setBorder(Border.ALL, BorderLineStyle.THICK);

        wsheet.addCell(new Label(0, curRow, title.getValue(), wcf1));
        wsheet.mergeCells(0, curRow, 6, curRow);

        wsheet.addCell(new Label(1, curRow + 1, "Nombre", wcf2));
        wsheet.setColumnView(1, 30);
        wsheet.addCell(new Label(2, curRow + 1, "JP", wcf2));
        wsheet.setColumnView(2, 15);
        wsheet.addCell(new Label(3, curRow + 1, "HT", wcf2));
        wsheet.setColumnView(3, 7);
        wsheet.addCell(new Label(4, curRow + 1, "HP", wcf2));
        wsheet.setColumnView(4, 7);
        wsheet.addCell(new Label(5, curRow + 1, "Estado", wcf2));
        wsheet.setColumnView(5, 12);
        wsheet.addCell(new Label(6, curRow + 1, "Descripcion", wcf2));
        wsheet.setColumnView(6, 70);
    }

    private static void _fillSheet(WritableSheet wsheet, ArrayList<IPdPItem> items) throws Exception {

        for (IPdPItem item : items) {
            switch (item.getType()) {
                case TITLE:
                    _fillItemType_Title(wsheet, item);
                    break;
                case PROJECT:
                    _fillItemType_Project(wsheet, item);
                    break;
                case FIX_NOTE:
                    break;
                default:
                    System.out.println("Item type unknown: " + item.getTypeName());
                    break;
            }
        }
    }

}
