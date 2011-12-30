/**
 * 
 */
package com.jzb.ipa.bundle;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.jzb.futil.FileExtFilter;

/**
 * @author n000013
 * 
 */
public class BundleReader {

    private IBundleDataParser m_binaryParser = new BinaryBundleParser();
    private IBundleDataParser m_xmlParser    = new XMLBundleParser();

    public BundleReader() {
    }

    public BundleData readBundleData(File bundleFile) throws Exception {
        return _processBundle(bundleFile);
    }

    public ArrayList<BundleData> readBundleDataFromFolder(File afolder, boolean recurseFolders) throws Exception {

        ArrayList<BundleData> dbList = new ArrayList<BundleData>();

        for (File afile : afolder.listFiles(new FileExtFilter(true, "ipa"))) {
            if (afile.isDirectory()) {
                if (recurseFolders) {
                    dbList.addAll(readBundleDataFromFolder(afile, recurseFolders));
                }
            } else {
                dbList.add(readBundleData(afile));
            }
        }

        return dbList;
    }

    private BundleData _processBundle(File bundleFile) throws Exception {

        BundleData bdata = null;
        byte[] img = null;
        ZipEntry zentry = null;
        ZipInputStream zis = new ZipInputStream(new FileInputStream(bundleFile));

        try {

            boolean plistProcessed = false, imageProcessed = false;
            for (;;) {
                zentry = zis.getNextEntry();
                if (zentry == null)
                    break;

                if (!plistProcessed && zentry.getName().endsWith(".app/Info.plist")) {
                    bdata = _readData(zentry, zis);
                    if (bdata != null)
                        plistProcessed = true;
                }

                if (!imageProcessed && zentry.getName().endsWith("iTunesArtwork")) {
                    img = _readBuffer(zentry, zis);
                    if (img != null)
                        imageProcessed = true;
                }

                if (plistProcessed && imageProcessed) {
                    break;
                }

            }

            if (bdata != null)
                bdata.image = img;

            return bdata;

        } finally {

            if (zis != null)
                zis.close();

        }

    }

    private byte[] _readBuffer(ZipEntry zentry, InputStream is) throws Exception {

        int size = (int) zentry.getSize();
        if (size == -1)
            size = 2048;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[size];
        while (is.available() > 0) {
            int lread = is.read(buffer);
            if (lread != -1) {
                baos.write(buffer, 0, lread);
            }
        }
        baos.close();

        return baos.toByteArray();
    }

    private BundleData _readData(ZipEntry zentry, InputStream is) throws Exception {

        BundleData bdata;

        byte buffer[] = _readBuffer(zentry, is);
        if (buffer == null)
            return null;

        String magicNumber = new String(buffer, 0, 6);
        if (magicNumber.equals("bplist")) {
            bdata = m_binaryParser.parse(buffer);
        } else {
            bdata = m_xmlParser.parse(buffer);
        }

        bdata.time = new Date(zentry.getTime());
        return bdata;

    }

}
