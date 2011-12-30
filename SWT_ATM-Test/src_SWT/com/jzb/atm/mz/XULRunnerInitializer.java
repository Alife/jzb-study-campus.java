package com.jzb.atm.mz;

import java.io.File;

import com.jzb.util.DefaultHttpProxy;

public class XULRunnerInitializer {

    static final String        XULRUNNER_PATH      = "org.eclipse.swt.browser.XULRunnerPath";             //$NON-NLS-1$

    public static final String XULRUNNER_DIRECTORY = "C:/WKSPs/Consolidado/XULRunner/mozilla/xulrunner";

    public static void initialize() {
        File xulRunnerRoot = null;
        try {
            xulRunnerRoot = new File(XULRUNNER_DIRECTORY);
        } catch (Exception e) {
        }

        if (xulRunnerRoot != null) {
            System.setProperty(XULRUNNER_PATH, xulRunnerRoot.getAbsolutePath());

            DefaultHttpProxy.setDefaultProxy();
        }

    }
}
