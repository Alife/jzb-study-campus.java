/**
 * 
 */
package com.jzb.mp3;

import java.io.File;

/**
 * @author n63636
 * 
 */

public class FileInfo {

    public File          file;
    public String        title;
    public FileInfoState state = FileInfoState.PENDING;

    public FileInfo() {
    }

    public FileInfo(File f) {
        file = f;
        title = f.getName();
    }
}
