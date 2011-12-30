/**
 * 
 */
package com.jzb.wiki.pdp;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.jzb.util.Des3Encrypter;
import com.jzb.wiki.util.BKSWikiHelper;

/**
 * @author n000013
 * 
 */
public class ParsePdP {

    private BKSWikiHelper m_helper;

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
            ParsePdP me = new ParsePdP();
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

        System.out.println("*** Reading info about PdP from Wiki BKS");
        m_helper = new BKSWikiHelper();
        m_helper.login(Des3Encrypter.decryptStr("PjN1Jb0t6CY0Eo9zcFVohw=="), Des3Encrypter.decryptStr("PjN1Jb0t6CYD25gJXVCyxw=="));

        TreeMap<String, ArrayList<IPdPItem>> releases = _getReleaseNames();
        for (Map.Entry<String, ArrayList<IPdPItem>> entry : releases.entrySet()) {
            ArrayList<IPdPItem> items = _getPdPReleaseInfo(entry.getKey());
            entry.setValue(items);
        }

        ExcelWriterCompact.writeInfoToExcel(releases);
        ExcelWriter.writeInfoToExcel(releases);
        XMLWriter.writeInfoToXML(releases);

    }

    private TreeMap<String, ArrayList<IPdPItem>> _getReleaseNames() throws Exception {

        TreeMap<String, ArrayList<IPdPItem>> rns = new TreeMap<String, ArrayList<IPdPItem>>();

        m_helper.navigateTo("?title=Plan_de_producto&action=edit", "Editando Plan de producto - Previsualizar - banksphereWiki");
        BufferedReader br = new BufferedReader(new StringReader(m_helper.getEditingText()));
        while (br.ready()) {
            String line = br.readLine();

            if (line == null)
                break;

            if (line.replace('_', ' ').contains("== [[Plan de producto Prjs")) {
                
                if (!line.contains("V3.4"))
                    continue;
                int p1 = line.lastIndexOf('[');
                int p2 = line.indexOf('|');
                String releaseName = line.substring(p1 + 1, p2).trim();
                rns.put(releaseName, null);
            }
        }
        br.close();

        return rns;
    }

    private ArrayList<IPdPItem> _getPdPReleaseInfo(String releaseName) throws Exception {
        m_helper.navigateTo("?title=" + releaseName , releaseName.replace('_', ' ') + " - banksphereWiki");
        PdPInfoParser pdpParser = new PdPInfoParser(m_helper.getText());
        System.out.println("  *** Parsing page info");
        ArrayList<IPdPItem> items = pdpParser.parse();
        return items;
    }

}
