package com.jzb.atm.mz;

import java.io.File;

import com.jzb.util.DefaultHttpProxy;

public class XULRunnerInitializer {

    static final String        XULRUNNER_PATH      = "org.eclipse.swt.browser.XULRunnerPath";                             //$NON-NLS-1$

    public static final String XULRUNNER_DIRECTORY = 
            //"/Users/jzarzuela/Documents/java-Campus/XULRunner/mozilla/xulrunner";

    "Library/Frameworks/XUL.framework/Versions/1.9.0.17";
    
    public static void initialize() {
        File xulRunnerRoot = new File(XULRUNNER_DIRECTORY);
        if (xulRunnerRoot != null) {
            //System.setProperty(XULRUNNER_PATH, xulRunnerRoot.getAbsolutePath());

            DefaultHttpProxy.setDefaultProxy();
        }

    }
}
