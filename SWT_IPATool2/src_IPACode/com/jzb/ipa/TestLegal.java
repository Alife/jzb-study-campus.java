/**
 * 
 */
package com.jzb.ipa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.jzb.ipa.plist.BinaryPListParser;
import com.jzb.ipa.plist.IPListParser;
import com.jzb.ipa.plist.T_PLDict;
import com.jzb.ipa.plist.XMLPListParser;

/**
 * @author n63636
 * 
 */
public class TestLegal {

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

    private IPListParser     m_binParser = new BinaryPListParser();
    private IPListParser     m_xmlParser = new XMLPListParser();
    private SimpleDateFormat m_sdf       = new SimpleDateFormat("yyyy-MM-dd");

    public boolean _readLegalInfo(File afile) throws Exception {

        ZipFile zf = new ZipFile(afile);
        Enumeration en = zf.getEntries();
        while (en.hasMoreElements()) {

            ZipEntry zentry = (ZipEntry) en.nextElement();

            if (zentry.getName().endsWith("iTunesMetadata.plist")) {
                
                byte buffer[] = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));

                T_PLDict dict = null;

                if (isBinaryBundle(buffer))
                    dict = m_binParser.parsePList(buffer);
                else
                    dict = m_xmlParser.parsePList(buffer);

                String appleId=dict.getStrValue("appleId");
                String purchaseDate=dict.getStrValue("purchaseDate");
                
                // Es legal si existe una de los dos
                return (appleId!=null || purchaseDate!=null);
            }

        }
        zf.close();

        return false;
    }

    private byte[] _readBuffer(long size, InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[(int) size];
        while (is.available() > 0) {
            int lread = is.read(buffer);
            if (lread != -1) {
                baos.write(buffer, 0, lread);
            }
        }
        baos.close();
        return baos.toByteArray();
    }

    private static final byte BPLIST00[] = { 98, 112, 108, 105, 115, 116, 48, 48 };

    private boolean isBinaryBundle(byte buffer[]) {
        for (int n = 0; n < BPLIST00.length; n++) {
            if (buffer[n] != BPLIST00[n])
                return false;
        }
        return true;
    }
}
