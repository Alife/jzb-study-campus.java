/**
 * 
 */
package com.jzb.img.tsk;

import java.io.File;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.util.Tracer;

/**
 * @author jzarzuela
 * 
 */
public class SplitByCompoundName extends BaseTask {

    // --------------------------------------------------------------------------------------------------------
    public SplitByCompoundName(JustCheck justChecking, File baseFolder, RecursiveProcessing recursive) {
        super(justChecking, baseFolder, recursive, true);
    }

    // --------------------------------------------------------------------------------------------------------
    public void splitByCompoundName() {
        try {
            _checkBaseFolder();
            _splitByCompoundName(m_baseFolder);
        } catch (Exception ex) {
            Tracer._error("Error processing action", ex);
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _splitByCompoundName(File folder) throws Exception {

        Tracer._debug("");
        Tracer._debug("Splitting files with compound name into subfolders (No recursive processing).");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = folder.listFiles(FileExtFilter.imgFilter(IncludeFolders.NO));

        for (File fImg : fList) {
            if (NameComposer.isCompoundName(fImg.getName())) {

                m_nc.parse(fImg.getName());

                StringBuffer newName = new StringBuffer();

                for (String groupName : m_nc.getGroupNames()) {
                    newName.append(groupName).append(File.separator);
                }

                boolean firstOne = true;
                for (String subgroupName : m_nc.getSubgroupNames()) {
                    if (firstOne) {
                        newName.append(RenameWithFolders.SUBGROUP_MARKER);
                        firstOne = false;
                    }
                    newName.append(subgroupName).append(File.separator);
                }

                newName.append(fImg.getName());

                File newFile = new File(m_baseFolder, newName.toString());

                _renameFile(fImg, newFile);
            }
        }

    }
}
