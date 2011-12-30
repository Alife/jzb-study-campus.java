/**
 * 
 */
package com.jzb.ipa;

import java.io.File;

import com.jzb.ipa.ssh.IInstMonitor;
import com.jzb.ipa.ssh.SSHIPAInstaller;
import com.jzb.util.Des3Encrypter;

/**
 * @author n000013
 * 
 */
public class InstallTester {

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
            InstallTester me = new InstallTester();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
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

        SSHIPAInstaller ipaInst = new SSHIPAInstaller();
        ipaInst.connect("127.0.0.1", Des3Encrypter.decryptStr("i84ommBJaBQ="), Des3Encrypter.decryptStr("WvO3H3yRW2nPsNpQjcGPFA=="));

        File ipaFile = new File("E:\\jb-apps\\_Apps\\Curiosos\\crk_N[AquaForest]_PK[jp.co.hudson.AquaForest]_V[1.0.1]_OS[2.0]_D[2008-11].ipa");
        ipaInst.installIPABundle(ipaFile, m_instMonitor);

        ipaInst.resetSpringBoard();
        ipaInst.disconnect();

    }

    IInstMonitor m_instMonitor = new IInstMonitor() {

                                   private int m_nextPercentage=5;
                                   
                                   public void installingBundle() {
                                       System.out.println("MONITOR - installingBundle");
                                   }

                                   public void processBegin() {
                                       System.out.println("MONITOR - processBegin");
                                   }

                                   public void processEnd(boolean failed) {
                                       System.out.println("MONITOR - processEnd");
                                   }

                                   public void sendFileBegin() {
                                       System.out.println("MONITOR - sendFileBegin");
                                       m_nextPercentage=5;
                                   }

                                   public void sendFileEnd() {
                                       System.out.println("\nMONITOR - sendFileEnd");
                                   }

                                   public void sendFileProgress(int percentage) {
                                       if(percentage>=m_nextPercentage) {
                                           System.out.print(percentage + "%  ");
                                           m_nextPercentage+=5;
                                       }
                                   }
                               };
}
