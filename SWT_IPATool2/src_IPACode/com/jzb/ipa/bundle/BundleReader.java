/**
 * 
 */
package com.jzb.ipa.bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class BundleReader {

    private IPListParser     m_binParser = new BinaryPListParser();
    private IPListParser     m_xmlParser = new XMLPListParser();
    private SimpleDateFormat m_sdf       = new SimpleDateFormat("yyyy-MM-dd");

    public BundleReader() {
    }

    public T_BundleData readInfo(File afile) throws Exception {

        T_BundleData data = new T_BundleData();

        boolean plistProcessed = false, imageProcessed = false, imdProcessed = false;

        ZipFile zf = new ZipFile(afile);
        Enumeration en = zf.getEntries();
        while (en.hasMoreElements()) {

            ZipEntry zentry = (ZipEntry) en.nextElement();

            if (!imdProcessed && zentry.getName().endsWith("iTunesMetadata.plist")) {
                byte buffer[] = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                T_PLDict dict2 = null;
                if (isBinaryBundle(buffer))
                    dict2 = m_binParser.parsePList(buffer);
                else
                    dict2 = m_xmlParser.parsePList(buffer);

                // Es legal si existe una de los dos
                String appleId1 = dict2.getStrValue("appleId");
                String appleId2 = dict2.getStrValue("com.apple.iTunesStore.downloadInfo/accountInfo/AppleID");
                String purchaseDate = dict2.getStrValue("com.apple.iTunesStore.downloadInfo/purchaseDate");
                if (appleId1 == null && appleId2 == null && purchaseDate == null) {
                    data.isLegal = '$';
                } else {
                    if ((appleId1 != null && appleId1.toLowerCase().contains("jzarzuela")) || (appleId2 != null && appleId2.toLowerCase().contains("jzarzuela"))) {
                        data.isLegal = '_';
                    } else {
                        data.isLegal = '#';
                    }
                }

                imdProcessed = true;
            }

            if (!plistProcessed && zentry.getName().endsWith(".app/Info.plist")) {
                byte buffer[] = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                if (isBinaryBundle(buffer))
                    data.dict = m_binParser.parsePList(buffer);
                else
                    data.dict = m_xmlParser.parsePList(buffer);
                data.fdate = m_sdf.format(new Date(zentry.getTime()));
                plistProcessed = true;
            }

            if (!imageProcessed && zentry.getName().endsWith("iTunesArtwork")) {
                data.img = _readBuffer((int) zentry.getSize(), zf.getInputStream(zentry));
                imageProcessed = true;
            }

            if (plistProcessed && imageProcessed && imdProcessed) {
                break;
            }
        }

        zf.close();

        return data;
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
