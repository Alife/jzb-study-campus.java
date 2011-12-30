/**
 * 
 */
package com.jzb.tools;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jzb.futil.FileExtFilter;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class RenumWorker extends BaseWorker {

    private static final int    COUNTER_INCREMENT = 5;
    private static final String TMP_PREFIX        = "$TMP_";

    // Used to find the very first 4 digits in the current name
    private Pattern             m_counterRegExpr  = Pattern.compile("[-_][0-9][0-9][0-9][0-9]*[-_]", Pattern.CASE_INSENSITIVE);

    private Pattern             m_dateRegExpr     = Pattern.compile("\\$[0-9]*-[0-9]*-[0-9]*=[0-9]*_[0-9]*_[0-9]*\\$", Pattern.CASE_INSENSITIVE);

    private DecimalFormat       m_df              = new DecimalFormat("00000");

    public RenumWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void renum(final String baseFolderStr, final int baseCounter, final boolean resetByFolder, final boolean groupedByName, final String folderFilter, final boolean toBeExcluded) {

        final Pattern regExpr = (folderFilter == null || folderFilter.length() == 0) ? null : Pattern.compile(folderFilter, Pattern.CASE_INSENSITIVE);

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._debug("");
                Tracer._debug("***************************************************************");
                Tracer._info("Renumbering pass 1");
                Tracer._debug("");
                _renum1(new File(baseFolderStr), baseCounter, resetByFolder, groupedByName, regExpr, toBeExcluded);

                if (!m_justChecking) {
                    Tracer._debug("");
                    Tracer._debug("***************************************************************");
                    Tracer._info("Renumbering pass 2");
                    Tracer._debug("");
                    _renum2(new File(baseFolderStr), regExpr, toBeExcluded);
                }

                return null;

            }
        };

        _makeCall(baseFolderStr, callable);

    }

    private int _renum1(final File baseFolder, final int baseCounter, final boolean resetByFolder, final boolean groupedByName, final Pattern folderFilter, final boolean toBeExcluded)
            throws Exception {

        boolean folderMatches = false, processFiles = true;
        int counter = baseCounter;
        String lastBaseName = "%&#%#$!";

        // before processing files, check if the folder must be filtered
        if (folderFilter != null) {
            folderMatches = folderFilter.matcher(baseFolder.getAbsolutePath()).find();
            processFiles = (folderMatches && !toBeExcluded) || (!folderMatches && toBeExcluded);
        }

        // Gets just image files and folders
        File fList[] = baseFolder.listFiles(FileExtFilter.imgFilter(true));

        // Split them and short the files to establish the counting properly
        TreeSet<File> allFiles = new TreeSet<File>();
        TreeSet<File> subFolders = new TreeSet<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory() && (!folderMatches || !toBeExcluded)) {
                subFolders.add(fImg);
            } else if (processFiles) {
                allFiles.add(fImg);
            }
        }

        if (processFiles) {
            Tracer._debug("");
            Tracer._info("Processing files in folder: '" + baseFolder + "'");
            Tracer._debug("");
        }

        // Iterate files
        for (File fImg : allFiles) {

            // First check if the file has set the read-only attribute to avoid changing its name
            if (!fImg.canWrite()) {
                Tracer._warn("File cannot be written, maybe it's read-only: '" + fImg.getName() + "'");
                continue;
            }

            String fName = fImg.getName();

            Matcher matcher = m_counterRegExpr.matcher(fName);
            if (!matcher.find())
                continue;

            int s = matcher.start() + 1;
            int e = matcher.end() - 1;
            String bName = fName.substring(0, s);

            // remove "date pattern" if present
            String currentBaseName = bName;
            Matcher matcher2 = m_dateRegExpr.matcher(bName);
            if (matcher2.find()) {
                currentBaseName = bName.substring(matcher2.end());
            }

            // Increments counter before of using it
            counter += COUNTER_INCREMENT;

            // Check grouping counter
            if (groupedByName && !currentBaseName.equals(lastBaseName)) {
                lastBaseName = currentBaseName;
                counter = baseCounter + COUNTER_INCREMENT;
            }

            String newName = bName + m_df.format(counter) + fName.substring(e);
            if (!newName.startsWith(TMP_PREFIX))
                newName = TMP_PREFIX + newName;


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
            if (resetByFolder) {
                _renum1(folder, baseCounter, resetByFolder, groupedByName, folderFilter, toBeExcluded);
            } else {
                counter = _renum1(folder, counter, resetByFolder, groupedByName, folderFilter, toBeExcluded);
            }
        }

        // Returns current COUNTER
        return counter;
    }

    private void _renum2(final File baseFolder, final Pattern folderFilter, final boolean toBeExcluded) throws Exception {

        boolean folderMatches = false, processFiles = true;

        // before processing files, check if the folder must be filtered
        if (folderFilter != null) {
            folderMatches = folderFilter.matcher(baseFolder.getAbsolutePath()).find();
            processFiles = (folderMatches && !toBeExcluded) || (!folderMatches && toBeExcluded);
        }

        // Gets just image files and folders
        File fList[] = baseFolder.listFiles(FileExtFilter.imgFilter(true));

        // Split them and short the files to establish the counting properly
        TreeSet<File> allFiles = new TreeSet<File>();
        ArrayList<File> subFolders = new ArrayList<File>();
        for (File fImg : fList) {
            if (fImg.isDirectory() && (!folderMatches || !toBeExcluded)) {
                subFolders.add(fImg);
            } else if (processFiles) {
                allFiles.add(fImg);
            }
        }

        if (processFiles) {
            Tracer._debug("");
            Tracer._info("Processing files in folder: '" + baseFolder + "'");
            Tracer._debug("");
        }

        // Iterate files
        for (File fImg : allFiles) {

            // First check if the file has set the read-only attribute to avoid changing its name
            if (!fImg.canWrite()) {
                // A trace was already shown before... to avoid doing it twice
                // Tracer._warn("File cannot be written, maybe it's read-only: '" + fImg.getName() + "'");
                continue;
            }

            String fName = fImg.getName();
            if (!fName.startsWith(TMP_PREFIX))
                continue;

            String newName = fName.substring(TMP_PREFIX.length());

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
            _renum2(folder, folderFilter, toBeExcluded);
        }

    }

}
