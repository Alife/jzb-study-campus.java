/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

import com.jzb.futil.FileExtFilter;
import com.jzb.ipa.bundle.BundleData;
import com.jzb.ipa.bundle.BundleReader;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;


/**
 * @author n000013
 * 
 */
public class ProcessBundlesWorker extends BaseWorker {

    private static final String     IPA_NAME_PATTERN = "N[%BN%]_PK[%BPKGID%]_V[%BV%]_OS[%BMOSV%]_D[%BTM%]";

    private static SimpleDateFormat s_sdf            = new SimpleDateFormat("yyyy-MM");

    private BundleReader            m_bundleReader   = new BundleReader();

    public ProcessBundlesWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void processFolder(final String baseFolderStr, final boolean recurseFolders, final boolean extractImage) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("** Processing IPA files from base folder: '" + baseFolderStr + "'");
                _processFolder(new File(baseFolderStr), recurseFolders, extractImage);
                Tracer._info("** Processing IPA files from base folder done.");
                return null;
            }
        };

        _makeCall(baseFolderStr, callable);
    }

    private void _extractImage(File afile, String newNameWithoutExtension, byte buffer[]) {

        try {
            String jpgName = newNameWithoutExtension + ".jpg";
            FileOutputStream fos = new FileOutputStream(new File(afile.getParent(), jpgName), false);
            fos.write(buffer);
            fos.close();
            Tracer._debug("Image extracted: '" + jpgName + "'");
        } catch (Exception ex) {
            Tracer._error("Error extracting image for '" + afile + "'", ex);
        }

    }

    private String _getNewBundleName(BundleData bdata) {

        String newName = IPA_NAME_PATTERN;

        newName = newName.replace("%BTM%", s_sdf.format(bdata.time));
        newName = newName.replace("%BN%", bdata.name);
        newName = newName.replace("%BV%", bdata.version);
        newName = newName.replace("%BMOSV%", bdata.minOSVersion);
        newName = newName.replace("%BPKGID%", bdata.pkgID);

        return newName;
    }

    private void _processBundleFile(File afile, boolean extractImage) {

        try {
            Tracer._debug("Processing file: " + afile);

            BundleData bdata = m_bundleReader.readBundleData(afile);
            if (bdata != null) {

                String newName = _getNewBundleName(bdata);
                newName = newName.replace('?', '_').replace(':', '_');

                if (!m_justChecking) {
                    _renameFile(afile, newName);

                    if (extractImage)
                        _extractImage(afile, newName, bdata.image);
                }
            } else {
                Tracer._error("Error processing file: '" + afile + "'");
            }

        } catch (Exception ex) {
            Tracer._error("Error processing: " + afile, ex);
        }

    }

    private void _processFolder(final File afolder, final boolean recurseFolders, final boolean extractImage) {

        Tracer._debug("** Processing IPA files from folder: '" + afolder + "'");
        for (File afile : afolder.listFiles(new FileExtFilter(true, "ipa"))) {
            if (afile.isDirectory()) {
                if (recurseFolders)
                    _processFolder(afile, recurseFolders, extractImage);
            } else {
                _processBundleFile(afile, extractImage);
            }
        }
    }

    private void _renameFile(File afile, String newNameWithoutExtension) {

        String newName = newNameWithoutExtension + ".ipa";

        File newFile = null;
        if (newName.equals(afile.getName())) {
            Tracer._debug("File already has calculated name. Skipped");
        } else {
            newFile = new File(afile.getParentFile(), newName);
            if (afile.renameTo(newFile)) {
                Tracer._debug("File renamed to: '" + newName + "'");
            } else {
                Tracer._error("Error renaming file to: '" + newName + "'");
            }
        }
    }

}
