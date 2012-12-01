/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.text.SimpleDateFormat;

import com.jzb.ipa.plist.PListParser;
import com.jzb.ipa.plist.T_PLDict;

/**
 * @author n63636
 * 
 */
public class TestLegal {

    private PListParser      m_plistParser = new PListParser();

    private SimpleDateFormat m_sdf         = new SimpleDateFormat("yyyy-MM-dd");

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
            TestLegal me = new TestLegal();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    public boolean _readLegalInfo(File afile) throws Exception {

        T_PLDict dict = m_plistParser.parsePList(afile);
        if (dict != null) {

            String appleId = dict.getStrValue("appleId");
            String purchaseDate = dict.getStrValue("purchaseDate");

            // Es legal si existe una de los dos
            return (appleId != null || purchaseDate != null);
        }

        else {
            return false;
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

        File ipaFolder = new File("C:\\Users\\n63636\\Desktop\\IPAs");
        for (File f : ipaFolder.listFiles()) {
            System.out.println();
            System.out.println(f.getName());
            _checkIfLegal(f);
        }
    }

    private void _checkIfLegal(File ipaFile) throws Exception {
        boolean b = _readLegalInfo(ipaFile);
    }

}
