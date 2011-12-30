/**
 * 
 */
package com.jzb.ipa.bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author n000013
 * 
 */
public class Tester {

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
            Tester me = new Tester();
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
    public void doIt2(String[] args) throws Exception {

        File f = new File("C:\\Documents and Settings\\n000013\\Desktop\\Info.plist");
        FileInputStream fis = new FileInputStream(f);
        byte buffer[] = new byte[(int) f.length()];
        int p = 0;
        for (;;) {
            int l = fis.read(buffer, p, buffer.length - p);
            p += l;
            if (l <= 0)
                break;
        }
        fis.close();

        BinaryBundleParser binaryParser = new BinaryBundleParser();
        BundleData bd = binaryParser.parse(buffer);
        System.out.println(bd);

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

        File f = new File("C:\\Documents and Settings\\n000013\\Desktop\\info.plist");
        BinaryPListParser bplp = new BinaryPListParser();
        Document xml=bplp.parse(f);
        System.out.println(xml);
        

    }
}
