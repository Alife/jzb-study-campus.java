/**
 * 
 */
package com.jzb.futil.fprocs;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.futil.FileUtils;
import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class SplitByExtensionFileProcessor implements IFileProcessor {

    private File    m_destBaseFolder;
    private Pattern m_filteredRegExp;

    public SplitByExtensionFileProcessor(File destBaseFolder, String filteredExtRE) {

        m_destBaseFolder = destBaseFolder;
        if (filteredExtRE != null) {
            m_filteredRegExp = Pattern.compile(filteredExtRE, Pattern.CASE_INSENSITIVE | Pattern.LITERAL | Pattern.DOTALL);
        }

    }

    /**
     * @see com.jzb.futil.IFileProcessor#processFile(java.io.File, java.io.File)
     */
    @Override
    public void processFile(File f, File baseFolder) throws Exception {

        String ext = FileUtils.getExtension(f);
        if(m_filteredRegExp!=null) {
            Matcher m = m_filteredRegExp.matcher(ext);
            if(!m.matches())
                return;
        }

        String baseFileName = f.getPath().substring(baseFolder.getPath().length());
        File newFile = new File(m_destBaseFolder, baseFileName);

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

        String sDF = m_destBaseFolder.getAbsolutePath().toLowerCase();
        sDF = sDF.replace("\\","\\\\");
        fi.getParams().addFolderExcludeFilter(sDF);
    }

}
