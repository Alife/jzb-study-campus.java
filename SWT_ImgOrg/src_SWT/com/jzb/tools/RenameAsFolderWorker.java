/**
 * 
 */
package com.jzb.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class RenameAsFolderWorker extends BaseWorker {

    private static final String INITIAL_COUNTER = "_00000-";
    private Pattern             m_counterRegExpr  = Pattern.compile("[-_][0-9][0-9][0-9][0-9]*[-_]", Pattern.CASE_INSENSITIVE);

    public RenameAsFolderWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void rename(final String baseFolderStr) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("Renaming files as folder");
                _rename(new File(baseFolderStr));
                return null;
            }
        };

        _makeCall(baseFolderStr, callable);
    }

    private void _rename(final File baseFolder) throws Exception {

        // Gets just image files and folders
        File fList[] = baseFolder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Split them and short the files to establish the counting properly
        TreeSet<File> allFiles = new TreeSet<File>();
        ArrayList<File> subFolders = new ArrayList<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory()) {
                subFolders.add(fImg);
            } else {
                allFiles.add(fImg);
            }
        }

        Tracer._debug("");
        Tracer._info("Processing files in folder: '" + baseFolder + "'");
        Tracer._debug("");

        // Iterate files
        for (File fImg : allFiles) {

            // First check if the file has set the read-only attribute to avoid changing its name
            if(!fImg.canWrite()) {
                Tracer._warn("File cannot be written, maybe it's read-only: '" + fImg.getName() + "'");
                continue;
            }
            
            String fName = fImg.getName();
            String parentName = fImg.getParentFile().getName();

            // Check if was already renamed
            Matcher matcher = Pattern.compile("^" + parentName + "_[0-9]*-").matcher(fName);
            if (matcher.find())
                continue;

            // New name depends on whether it already has a "counter"
            String newName;
            Matcher matcher2 = m_counterRegExpr.matcher(fName);
            if (matcher2.find()) {
                int ss =matcher2.start();
                int ee=matcher2.end();
                
                String cc = fName.substring(ss,ee);
                
                String baseFName=fName.substring(0,ss)+"-"+fName.substring(ee);
                
                newName = parentName + cc + baseFName;
            }
            else {
                newName = parentName + INITIAL_COUNTER + fName;
            }

            File newFile = new File(fImg.getParentFile(), newName);
            boolean done = m_justChecking ? false : fImg.renameTo(newFile);
            if (m_justChecking || done) {
                Tracer._debug("File renamed from '" + fImg.getName() + "' to '" + newFile.getName() + "' in folder '" + fImg.getParent() + "'");
            } else {
                Tracer._error("Error renaming file '" + fImg.getName() + "' to '" + newFile.getName() + "' in folder '" + fImg.getParent() + "'");
            }
        }

        // Iterate subfolders
        for (File folder : subFolders) {
            _rename(folder);
        }

    }
}
