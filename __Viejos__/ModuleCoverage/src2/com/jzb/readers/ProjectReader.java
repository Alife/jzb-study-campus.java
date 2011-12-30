/**
 * Rigel Services Model Infrastructure, Version 1.0
 *
 * Copyright (C) 2002 ISBAN.
 * All Rights Reserved.
 *
 **/

package com.jzb.readers;

import java.io.File;
import java.io.FileFilter;



/**
 * @author PS00A501
 *
 */
public class ProjectReader {

    private static FileFilter ff = new FileFilter() {
        /**
         * @see java.io.FileFilter#accept(java.io.File)
         */
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(".xmlvb") || file.getName().endsWith(".xmlopi") || file.getName().endsWith(".xmlopp") || file.getName().endsWith(".xmlfac") || file.getName().endsWith(".xmlscen");
        }
    };
    
    public void readPrjElements(File basePath) throws Exception {
        File files[] = basePath.listFiles(ff);
        for (int n = 0; n < files.length; n++) {
            if (files[n].isDirectory()) {
                readPrjElements(files[n]);
            } else {
                Impl_Loader.newInstance(files[n]).load();
            }
        }
    }
}
