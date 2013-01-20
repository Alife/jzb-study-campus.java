/**
 * 
 */
package kkkkk.com.jzb.ipa.plist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;

/**
 * @author jzarzuela
 * 
 */
public class TestParser {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            TestParser me = new TestParser();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
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

        String plistFile = "/Users/jzarzuela/Downloads/_tmp_/kk/mal_Info.plist";
        //File ipaFile = new File("/Users/jzarzuela/Documents/personal/iPhone/IPAs/_legal/iPhone/_games/Golden Eggs 1.0.ipa");

        NSDictionary cosa = (NSDictionary)PropertyListParser.parse(new File(plistFile));
        System.out.println(cosa.toXMLPropertyList());
        System.exit(1);
        
        
        T_PLDict dict;
        PListParser parser = new PListParser();

        byte buffer[] = _readBuffer(plistFile);
        dict = parser.parsePList(buffer);
        System.out.println(dict);

    }

    private byte[] _readBuffer(String plistFile) throws IOException {

        File fpl = new File(plistFile);
        byte buffer[] = new byte[(int) fpl.length()];
        FileInputStream fis = new FileInputStream(fpl);
        int lread = fis.read(buffer);
        fis.close();
        if (lread != buffer.length) {
            throw new IOException("Error leyendo el fichero");
        }
        return buffer;
    }

}
