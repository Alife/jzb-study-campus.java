/**
 * 
 */
package com.jzb.tools;

import java.io.File;
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
public class MoveToFolderWorker extends BaseWorker {

    private Pattern m_counterRegExpr = Pattern.compile("[-_][0-9][0-9][0-9][0-9]*[-_]", Pattern.CASE_INSENSITIVE);

    // private Pattern m_folderNameRE = Pattern.compile("[a-z]*_[0-9]*-([a-z0-9 ])[-_](.*)", Pattern.CASE_INSENSITIVE);

    public MoveToFolderWorker(boolean justChecking,  IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void separateInFolders(final String baseFolderStr) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("Separating files into subfolders");
                _separateInFolders(new File(baseFolderStr));
                return null;
            }
        };

        _makeCall(baseFolderStr, callable);
    }

    public void putFilesTogether(final String baseFolderStr) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("Putting all files together from subfolders");
                _putFilesTogether(new File(baseFolderStr));
                return null;

            }
        };

        _makeCall(baseFolderStr, callable);
    }

    private void _separateInFolders(final File baseFolder) throws Exception {

        // Gets just image files and folders
        File fList[] = baseFolder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Iterate files
        for (File aFile : fList) {

            if (aFile.isDirectory())
                continue;

            // First check if the file has set the read-only attribute to avoid changing its name
            if (!aFile.canWrite()) {
                Tracer._warn("File cannot be written, maybe it's read-only: '" + aFile.getName() + "'");
                continue;
            }

            String folderName = _getNewFolderName(aFile);
            if (folderName == null) {
                Tracer._warn("Cannot extract folder name from file name: '" + aFile.getName() + "'");
                continue;
            }

            File newFolder = new File(baseFolder, folderName);
            newFolder.mkdirs();

            File newfile = new File(newFolder, aFile.getName());

            boolean done = m_justChecking ? false : aFile.renameTo(newfile);
            if (m_justChecking || done) {
                Tracer._debug("File moved to subfolder '" + folderName + "'. File: '" + aFile + "'");
            } else {
                Tracer._error("Error moving file to subfolder '" + folderName + "'. File: '" + aFile + "'");
            }

        }

    }

    private String _getNewFolderName(File afile) throws Exception {

        String fileName = afile.getName();
        String foldersPath;

        // Elimates the "img_xxxx" ending. Compares with ">0" to check that there is something before "img_xxx" in the fname
        int p1;
        p1 = fileName.toLowerCase().lastIndexOf("img");
        if (p1 > 0) {
            fileName = fileName.substring(0, p1);
        }

        // Search for a counter to create several folders
        Matcher matcher = m_counterRegExpr.matcher(fileName);
        if (matcher.find()) {
            int s = matcher.start() + 1;
            int e = matcher.end() - 1;

            String s1 = _cleanName(fileName.substring(0, s));
            String s2 = _cleanName(fileName.substring(e));
            
            // If it has several subparts following the counter, just first is taken
            p1=s2.indexOf('-');
            if(p1>0)
                s2=s2.substring(0,p1);

            // check if new name is equals to current file's name to skip it
            if (!s1.equalsIgnoreCase(afile.getParentFile().getName())) {
                foldersPath = s1 + File.separatorChar + s2;
            } else {
                foldersPath = s2;
            }
        } else {
            foldersPath = fileName;
        }

        if (!foldersPath.equalsIgnoreCase(afile.getName()))
            return foldersPath;
        else
            return null;
    }

    private String _cleanName(String s) {

        while (s.startsWith("-") || s.startsWith("_")) {
            s = s.substring(1);
        }
        while (s.endsWith("-") || s.endsWith("_")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private void _putFilesTogether(final File baseFolder) throws Exception {

        // Iterates all subfolders in base folder
        for (File aFile : baseFolder.listFiles()) {
            if (aFile.isDirectory())
                _putFilesTogether2(baseFolder, aFile);
        }

    }

    private void _putFilesTogether2(final File baseFolder, final File aFolder) throws Exception {

        Tracer._debug("");
        Tracer._info("Moving files in folder: '" + aFolder + "'");
        Tracer._debug("");

        // Gets just image files and folders
        File fList[] = aFolder.listFiles(FileExtFilter.imgFilter(IncludeFolders.YES));

        // Iterate list
        for (File aFile : fList) {
            if (aFile.isDirectory()) {
                _putFilesTogether2(baseFolder, aFile);
            } else {

                // First check if the file has set the read-only attribute to avoid changing its name
                if (!aFile.canWrite()) {
                    Tracer._warn("File cannot be written, maybe it's read-only: '" + aFile.getName() + "'");
                    continue;
                }
                File newfile = new File(baseFolder, aFile.getName());

                boolean done = m_justChecking ? false : aFile.renameTo(newfile);
                if (m_justChecking || done) {
                    Tracer._debug("File moved to base folder. File: '" + aFile + "'");
                } else {
                    Tracer._error("Error moving file to base folder. File: '" + aFile + "'");
                }

            }
        }
    }
}
