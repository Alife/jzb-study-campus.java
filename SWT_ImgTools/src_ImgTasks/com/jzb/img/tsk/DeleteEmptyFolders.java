/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;

import com.jzb.futil.FileExtFilter;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class DeleteEmptyFolders extends BaseTask {

    // --------------------------------------------------------------------------------------------------------
    public DeleteEmptyFolders(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void deleteEmptyFolders() {

        try {
            _checkBaseFolder();
            _deleteEmptyFolders(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _deleteEmptyFolders(File folder) throws Exception {

        // Gets just folders
        File fList[] = folder.listFiles(FileExtFilter.folderFilter());

        if(fList==null) {
            return;
        }
        
        // ---------------------------------------------
        // Iterate subfolders
        for (File sfolder : fList) {
            _deleteEmptyFolders(sfolder);
        }

        
        Tracer._debug("");
        Tracer._debug("Deleting empty subfolders in folder: '" + folder + "'");
        Tracer._debug("");

        
        // ---------------------------------------------
        // Delete MAC OS X special files
        _deleteMacDS_Store(folder);
        
        // ---------------------------------------------
        // Delete empty subfolders
        for (File sfolder : fList) {
            if (sfolder.listFiles()!=null && sfolder.listFiles().length == 0) {
                _deleteFolder(sfolder, m_justChecking);
            } 
        }
    }
    
    // --------------------------------------------------------------------------------------------------------
    protected static void _deleteFolder(File folder, JustCheck justChecking) {
        boolean done = justChecking == JustCheck.YES ? false : folder.delete();
        if (justChecking == JustCheck.YES || done) {
            Tracer._debug("Deleted empty folder: '" + folder.getName() + "'");
        } else {
            Tracer._error("Deleting empty folder: '" + folder.getName() + "'");
        }
    }
    
    // --------------------------------------------------------------------------------------------------------
    protected static void _deleteMacDS_Store(File folder) {
        for (File f : folder.listFiles()) {
            if(f.getName().equals(".DS_Store")) {
                f.delete();
            }
        }
    }

}
