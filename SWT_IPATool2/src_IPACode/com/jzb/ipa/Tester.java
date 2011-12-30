/**
 * 
 */
package com.jzb.ipa;

import java.io.File;

import com.jzb.ipa.bundle.BundleReader;
import com.jzb.ipa.bundle.T_BundleData;
import com.jzb.ipa.ren.NameComposer;
import com.jzb.ipa.upd.IPAUpdater;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
@SuppressWarnings("unused")
public class Tester {

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
            Tester me = new Tester();
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
    public void doIt2(String[] args) throws Exception {

        File updateFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta");
        File ExistingFolder = new File("C:\\JZarzuela\\iPhone\\IPAs");
        File backupFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_backup");
        File newfFolder = new File("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\_newFiles");

        // IPARenamer renamer = new IPARenamer();
        // renamer.rename(folder,true);

        IPAUpdater updater = new IPAUpdater();
        updater.update(ExistingFolder, new File[] { updateFolder }, backupFolder, newfFolder);
    }

    public void doIt(String[] args) throws Exception {

        File f=new File("C:\\JZarzuela\\iPhone\\IPAs\\iPhone-iPad-mixtas\\_games\\mlN[Tiki Premium]_PK[com.spokko.TikiPremium]_V[1.5.3]_OS[3.0]_D[2011-04-21].ipa");
        Tracer._debug("Processing IPA file: " + f);
        BundleReader m_ipaReader = new BundleReader();
        T_BundleData ipaInfo = m_ipaReader.readInfo(f);
        String newName = NameComposer.composeName(ipaInfo);
        
        System.out.println(newName);
    }

}
