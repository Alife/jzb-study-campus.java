/**
 * 
 */
package kkkkk.com.jzb.ipa.plist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/**
 * @author jzarzuela
 * 
 */
public class PListParser implements IPListParser {

    private static final byte BPLIST00[]  = { 98, 112, 108, 105, 115, 116, 48, 48 };
    private IPListParser      m_binParser = new BinaryPListParser();

    private IPListParser      m_xmlParser = new XMLPListParser();

    public PListParser() {
    }

    public T_PLDict parsePList(byte buffer[]) throws Exception {

        if (isBinaryBundle(buffer))
            return m_binParser.parsePList(buffer);
        else
            return m_xmlParser.parsePList(buffer);
    }

    public T_PLDict parsePList(File ipaFile) throws Exception {

        byte buffer[] = null;

        // Busca la entrada en el IPA
        ZipFile zf = new ZipFile(ipaFile);
        Enumeration en = zf.getEntries();
        while (en.hasMoreElements()) {

            ZipEntry zentry = (ZipEntry) en.nextElement();

            if (zentry.getName().endsWith("iTunesMetadata.plist")) {
                buffer = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                break;
            }
        }
        zf.close();

        // Parsea el plist
        if (buffer == null) {
            return null;
        } else {
            return parsePList(buffer);

        }

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

    public static boolean isBinaryBundle(byte buffer[]) {
        for (int n = 0; n < BPLIST00.length; n++) {
            if (buffer[n] != BPLIST00[n])
                return false;
        }
        return true;
    }

}
