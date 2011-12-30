/**
 * 
 */
package com.jzb.wiki.pdp;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import com.jzb.wiki.util.NameValuePair;

/**
 * @author n000013
 * 
 */
public class XMLWriter {

    public static void writeInfoToXML(TreeMap<String, ArrayList<IPdPItem>> releases) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");

        String xmlFileName = "C:\\pdp_info-" + sdf.format(new Date()) + ".xml";
        System.out.println("Creating XML file: '" + xmlFileName + "'");
        PrintWriter pw = new PrintWriter(xmlFileName);

        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        pw.println("<PdP_Info>");

        for (Map.Entry<String, ArrayList<IPdPItem>> entry : releases.entrySet()) {

            String name = entry.getKey();
            int p1 = name.indexOf("Prjs_");
            if (p1 > 0) {
                name = name.substring(p1 + 5);
            }
            pw.println("  <PdP_Release name='" + name + "'>");
            _fillRelaseInfo(pw, entry.getValue());
            pw.println("  </PdP_Release name='" + name + "'>");
        }

        pw.println("</PdP_Info>");

        pw.flush();
        pw.close();
    }

    private static void _fillItemType_Project(PrintWriter pw, IPdPItem prj) throws Exception {

        pw.println("    <project>");
        for (NameValuePair vp : prj.getAttrs()) {
            pw.println("      <" + vp.getName() + ">" + vp.getValue() + "</" + vp.getName() + ">");
        }
        pw.println("    <project>");
    }

    private static void _fillItemType_Title(PrintWriter pw, IPdPItem title) throws Exception {
        pw.println("    <title>" + title.getValue() + "</title>");
    }

    private static void _fillRelaseInfo(PrintWriter pw, ArrayList<IPdPItem> items) throws Exception {

        for (IPdPItem item : items) {
            switch (item.getType()) {
                case TITLE:
                    _fillItemType_Title(pw, item);
                    break;
                case PROJECT:
                    _fillItemType_Project(pw, item);
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
