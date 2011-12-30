package com.jzb.ipa.ssh;

import java.io.File;

import com.jzb.futil.FileExtFilter;
import com.jzb.util.Des3Encrypter;


/**
 * 
 */

/**
 * @author n000013
 * 
 */
public class Test1 {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            Test1 me = new Test1();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(0);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        IInstMonitor mon = new IInstMonitor() {

            private boolean doneFirstProgress = false;

            public void installingBundle() {
                System.out.print("IPA Monitor - Installing bundle");
            }

            public void processBegin() {
                System.out.print("IPA Monitor - Process started");
            }

            public void processEnd(boolean failed) {
                System.out.print("IPA Monitor - Process done. Failed: " + failed);
            }

            public void sendFileBegin() {
                System.out.println("IPA Monitor - SendFileBegin");
            }

            public void sendFileEnd() {
                System.out.println("\nIPA Monitor - SendFileEnd");
            }

            public void sendFileProgress(int percentage) {
                if (!doneFirstProgress) {
                    doneFirstProgress = true;
                    System.out.print("IPA Monitor - SendFileProgress: " + percentage);
                } else {
                    System.out.print(" " + percentage);
                }
            }
        };

        SSHIPAInstaller ipaInst = new SSHIPAInstaller();

        ipaInst.connect("127.0.0.1", Des3Encrypter.decryptStr("i84ommBJaBQ="), Des3Encrypter.decryptStr("WvO3H3yRW2nPsNpQjcGPFA=="));

        File baseFolder = new File("C:\\Documents and Settings\\n000013\\Desktop\\jj\\");
        for (File ipaFile : baseFolder.listFiles(new FileExtFilter(false, "ipa"))) {
            ipaInst.installIPABundle(ipaFile, mon);
        }

        ipaInst.resetSpringBoard();
        ipaInst.disconnect();

    }

}
