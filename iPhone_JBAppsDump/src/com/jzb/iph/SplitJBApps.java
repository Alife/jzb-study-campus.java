/**
 * 
 */
package com.jzb.iph;

import java.io.File;
import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.ipa.plist.PListParser;
import com.jzb.ipa.plist.T_PLDict;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class SplitJBApps {

    private PListParser m_plistParser   = new PListParser();

    private String      m_legalFolder   = "_legal";
    private String      m_crackedFolder = "_cracked";
    private File        m_baseFolder    = new File("/Users/jzarzuela/Documents/personal/iPhone/IPAs");
    private int         m_baseFolderLen = m_baseFolder.getAbsolutePath().length();

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
            SplitJBApps me = new SplitJBApps();
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

        _processIPAFolder(m_baseFolder);

    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _processIPAFolder(File folder) throws Exception {

        if (folder.getName().equals(m_legalFolder) || folder.getName().equals(m_crackedFolder)) {
            return;
        }

        Tracer._debug("");
        Tracer._debug("----------------------------------------------------------------");
        Tracer._debug("Processing IPA folder: " + folder);
        Tracer._debug("----------------------------------------------------------------");
        Tracer._debug("");

        File fList[] = folder.listFiles(new FileExtFilter(IncludeFolders.YES, "ipa"));

        // Procesamos primero archivos
        for (File f : fList) {
            if (f.isDirectory())
                continue;

            _processIPA(f);
        }

        // Procesamos despues los subdirectorios
        for (File f : fList) {
            if (!f.isDirectory())
                continue;

            _processIPAFolder(f);
        }
        
        if(folder.listFiles().length==0) {
            folder.delete();
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------------
    private void _processIPA(File ipaFile) throws Exception {

        BundleInfo bi;

        Tracer._debug("IPAFile = '%s'", ipaFile.getName());
        T_PLDict dict = m_plistParser.parsePList(ipaFile);
        if (dict == null) {
            bi = new BundleInfo(ipaFile);
        } else {
            bi = new BundleInfo(ipaFile, dict);
        }

        File newFolder;

        if (bi.isCracked()) {
            newFolder = new File(m_baseFolder, m_crackedFolder);
        } else {
            newFolder = new File(m_baseFolder, m_legalFolder);
        }

        String fullName = ipaFile.getAbsolutePath().substring(m_baseFolderLen);
        File newFile = new File(newFolder, fullName);

        newFile.getParentFile().mkdirs();
        if (!ipaFile.renameTo(newFile)) {
            Tracer._error("Error moving file to: " + newFile);
        }

    }
}
