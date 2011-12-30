/**
 * 
 */
package com.jzb.futil;

import java.io.File;
import java.io.FileFilter;

/**
 * @author n000013
 * 
 */
public class FileExtFilter implements FileFilter {

    public static FileExtFilter imgFilter(boolean allowFolders) {
        return new FileExtFilter(allowFolders, "jpg", "gif", "png", "bmp", "cr2");
    }
    
    private String  m_AllowedExts[] = {};
    private boolean m_allowFolders;

    public FileExtFilter(boolean allowFolders, String... allowedExts) {
        m_allowFolders = allowFolders;

        m_AllowedExts = allowedExts;
    }

    /**
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
        return (m_allowFolders && file.isDirectory()) || checkFileExt(file);
    }

    private boolean checkFileExt(File file) {
        String ext = getFileExt(file);
        if (ext.length() == 0)
            return false;
        for (String s : m_AllowedExts) {
            if (s.equalsIgnoreCase(ext))
                return true;
        }
        return false;
    }

    private String getFileExt(File file) {
        int pos = file.getName().lastIndexOf('.');
        if (pos > 0) {
            return file.getName().substring(pos + 1).toLowerCase();
        } else {
            return "";
        }
    }
}
