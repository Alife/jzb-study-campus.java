/**
 * 
 */
package com.jzb.excel;

import java.util.ArrayList;

import com.jzb.pdp.WikiParser.PrjData;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * @author PS00A501
 * 
 */
public class BKS_Prjs {

    public BKS_Prjs() {
    }
    
    public void createSheet(WritableWorkbook owb, ArrayList<PrjData> prjs) throws Exception {
        WritableSheet wsheet = owb.createSheet("BKS-Prj", 0);
        createHeaders(wsheet);
        createDataCells(wsheet, prjs);
    }
    
    
    private void createHeaders(WritableSheet ws) throws Exception {

        // Create Header Format
        WritableCellFormat hdrFmt = new WritableCellFormat();
        hdrFmt.setBackground(Colour.YELLOW);
        hdrFmt.setAlignment(Alignment.CENTRE);
        hdrFmt.setVerticalAlignment(VerticalAlignment.CENTRE);
        hdrFmt.setBorder(Border.ALL, BorderLineStyle.THIN);
        hdrFmt.setWrap(true);
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 11);
        wf.setColour(Colour.BLACK);
        wf.setBoldStyle(WritableFont.BOLD);
        hdrFmt.setFont(wf);

        ws.addCell(new Label(1, 1, "Proyecto", hdrFmt));
        ws.addCell(new Label(2, 1, "JP", hdrFmt));
        ws.addCell(new Label(3, 1, "Estado", hdrFmt));
        ws.addCell(new Label(4, 1, "F.Merge", hdrFmt));

        ws.setColumnView(0, 4);
        ws.setColumnView(1, 60);
        ws.setColumnView(2, 14);
        ws.setColumnView(4, 10);
    }
    
    private void createDataCells(WritableSheet ws, ArrayList<PrjData> prjs) throws Exception {

        int row = 2;
        for (PrjData i : prjs) {
            row = createDataRow(ws, row, "", i);
        }
    }

    private int createDataRow(WritableSheet ws, int row, String pdpMainPrj, PrjData prj) throws Exception {

        // Create Format
        WritableCellFormat fmt = new WritableCellFormat();
        // fmt.setBackground(Colour.YELLOW);
        fmt.setAlignment(Alignment.LEFT);
        fmt.setVerticalAlignment(VerticalAlignment.CENTRE);
        fmt.setBorder(Border.ALL, BorderLineStyle.THIN);
        fmt.setWrap(true);
        WritableFont wf = new WritableFont(WritableFont.ARIAL, 11);
        // wf.setColour(Colour.RED);
        // wf.setBoldStyle(WritableFont.BOLD);
        fmt.setFont(wf);

        if (prj.subPrjs.size() > 0) {
            for (PrjData i : prj.subPrjs) {
                row = createDataRow(ws, row, prj.title+"\n    ", i);
            }
        } else {
           
            ws.addCell(new Label(1, row, pdpMainPrj+prj.title, fmt));
            ws.addCell(new Label(2, row, prj.JP, fmt));
            ws.addCell(new Label(3, row, "", fmt));
            ws.addCell(new Label(4, row, "", fmt));
            row++;
        }

        return row;
    }
}
