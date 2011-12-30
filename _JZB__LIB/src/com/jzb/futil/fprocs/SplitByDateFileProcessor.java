/**
 * 
 */
package com.jzb.futil.fprocs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class SplitByDateFileProcessor implements IFileProcessor {

    private SimpleDateFormat m_sdf = new SimpleDateFormat("dd-MM-yyyy");
    private File             m_destFolder;

    public SplitByDateFileProcessor(File destFolder) {

        m_destFolder = destFolder;

    }

    /**
     * @see com.jzb.futil.IFileProcessor#processFile(java.io.File, java.io.File)
     */
    @Override
    public void processFile(File f, File baseFolder) throws Exception {
        
        String dt = m_sdf.format(new Date(f.lastModified()));

        File newFile = new File(m_destFolder, dt + File.separatorChar + f.getName());

        newFile.getParentFile().mkdirs();
        if (!f.renameTo(newFile)) {
            Tracer._debug("  *> Error renaming '" + f + "' to '" + newFile + "'");
        }
    }

    /**
     * @see com.jzb.futil.IFileProcessor#setFolderIterator(com.jzb.futil.FolderIterator)
     */
    @Override
    public void setFolderIterator(FolderIterator fi) {

        String sDF = m_destFolder.getAbsolutePath().toLowerCase();
        sDF = sDF.replace("\\","\\\\");
        fi.getParams().addFolderExcludeFilter(sDF);
    }

}
