/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;
import java.util.TreeSet;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class Renumerate extends BaseTask {

    private static final int COUNTER_INCREMENT = 5;

    public static enum ResetByFolder {
        NO, YES
    }

    // --------------------------------------------------------------------------------------------------------
    public Renumerate(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive);
    }

    // --------------------------------------------------------------------------------------------------------
    public void renumerate(int baseCounter, ResetByFolder resetByFolder) {

        try {
            _checkBaseFolder();
            _renumerate(m_baseFolder, baseCounter, resetByFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private int _renumerate(File folder, int baseCounter, ResetByFolder resetByFolder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Renumerating file's names" + (m_recursive == RecursiveProcessing.YES ? " * RECURSIVELY * " : " ") + "in folder: '" + folder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split files and folders to process them properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        int counter = baseCounter;

        // ---------------------------------------------
        // Iterate files in subfolder
        for (File fImg : allFiles) {

            String fName = fImg.getName();
            m_nc.parse(fName);
            m_nc.setIndex(counter);
            counter += COUNTER_INCREMENT;
            String newName = m_nc.compose();
            File newFile = new File(fImg.getParentFile(), newName);
            _renameFile(fImg, newFile);
        }

        // ---------------------------------------------
        // Iterate subfolders
        if (m_recursive == RecursiveProcessing.YES) {
            for (File sfolder : subFolders) {
                if (sfolder.isDirectory()) {
                    if (resetByFolder == ResetByFolder.YES) {
                        _renumerate(sfolder, baseCounter, resetByFolder);
                    } else {
                        counter = _renumerate(sfolder, counter, resetByFolder);
                    }
                }
            }
        }

        return counter;

    }

}
