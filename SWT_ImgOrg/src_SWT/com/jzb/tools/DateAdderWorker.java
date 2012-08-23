/**
 * 
 */
package com.jzb.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.jzb.futil.FileExtFilter;
import com.jzb.futil.FileExtFilter.IncludeFolders;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class DateAdderWorker extends BaseWorker {

    private static final String NO_TIME_STR   = "$0000-00-00=00_00_00$-";
    private Pattern             m_checkDateRE = Pattern.compile("\\$[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]=[0-9][0-9]_[0-9][0-9]_[0-9][0-9]\\$-");
    private SimpleDateFormat    m_sdf         = new SimpleDateFormat("$yyyy-MM-dd=HH_mm_ss$-");

    public DateAdderWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void addDateToFiles(final String baseFolderStr, final int years, final int months, final int days, final int hours, final int mins, final int secs) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("Adding date to folder");
                _addDateToFiles(new File(baseFolderStr), years, months, days, hours, mins, secs);
                return null;
            }
        };

        _makeCall(baseFolderStr, callable);
    }

    public void removeDateFromFiles(final String baseFolderStr) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("Adding date to folder");
                _removeDateFromFiles(new File(baseFolderStr));
                return null;
            }
        };

        _makeCall(baseFolderStr, callable);
    }

    private void _addDateToFiles(final File baseFolder, final int years, final int months, final int days, final int hours, final int mins, final int secs) throws Exception {

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
            String newName;

            // Check if was already renamed
            Matcher matcher = m_checkDateRE.matcher(fName);
            if (matcher.find()) {
                newName = _getExifDateStr(fImg, years, months, days, hours, mins, secs) + fName.substring(NO_TIME_STR.length());
            } else {
                newName = _getExifDateStr(fImg, years, months, days, hours, mins, secs) + fName;
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
            _addDateToFiles(folder, years, months, days, hours, mins, secs);
        }

    }

    private String _getExifDateStr(File file, final int years, final int months, final int days, final int hours, final int mins, final int secs) {

        try {
            Metadata metadata = JpegMetadataReader.readMetadata(file);
            Directory dir = metadata.getDirectory(com.drew.metadata.exif.ExifDirectory.class);
            Date d = null;
            d = dir.getDate(36868);// EXIF_DATE_TIME);

            Calendar cal = Calendar.getInstance();
            cal.setTime(d);

            cal.add(Calendar.YEAR, years);
            cal.add(Calendar.MONTH, months);
            cal.add(Calendar.DAY_OF_MONTH, days);
            cal.add(Calendar.HOUR_OF_DAY, hours);
            cal.add(Calendar.MINUTE, mins);
            cal.add(Calendar.SECOND, secs);

            return m_sdf.format(cal.getTime());

        } catch (Exception e) {
            return NO_TIME_STR;
        }
    }

    private void _removeDateFromFiles(final File baseFolder) throws Exception {
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

            String fName = fImg.getName();

            // Check if wasn't already renamed
            Matcher matcher = m_checkDateRE.matcher(fName);
            if (!matcher.find())
                continue;

            String newName = fName.substring(NO_TIME_STR.length());

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
            _removeDateFromFiles(folder);
        }

    }
}
