/**
 * 
 */
package com.jzb.t2;

import java.io.File;
import java.io.FileFilter;

/**
 * @author n000013
 * 
 */
public class ImgFileFilter2 implements FileFilter {

    private static String ALLOWED_EXTS[] = { "cr2", "jpg", "gif", "png", "bmp" };

    /**
     * @see java.io.FileFilter#accept(java.io.File)
     */
    public boolean accept(File file) {
        return file.isDirectory() || checkFileExt(file);
    }

    private boolean checkFileExt(File file) {
        String ext = getFileExt(file);
        if (ext.length() == 0)
            return false;
        for (String s : ALLOWED_EXTS) {
            if (s.equals(ext))
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
