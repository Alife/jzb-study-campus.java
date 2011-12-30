/**
 * 
 */
package com.jzb.futil.fprocs;

import java.io.File;

import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class DummyFileProcessor implements IFileProcessor {

    public static IFileProcessor instance = new DummyFileProcessor();

    private DummyFileProcessor() {
    }

    /**
     * @see com.jzb.futil.IFileProcessor#setFolderIterator(com.jzb.futil.FolderIterator)
     */
    @Override
    public void setFolderIterator(FolderIterator fi) {
    }
    
    /**
     * @see com.jzb.futil.IFileProcessor#processFile(java.io.File, java.io.File)
     */
    @Override
    public void processFile(File f, File baseFolder) throws Exception {
        Tracer._debug("  File to be processed: '" + f + "'");
    }

}
