/**
 * 
 */
package com.jzb.ipa.chk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;

import com.jzb.futil.FileExtFilter;


/**
 * @author n000013
 * 
 */
public class CheckTest {

    private static final String IPA_BUNDLE_NAMES_FILE = "ipaBundle.names";

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
            CheckTest me = new CheckTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    public void doIt(String[] args) throws Exception {

        File baseFolder = new File("W:\\iphone\\_FINISHED_\\crk-apps");

        T_BNamesInfo discarted = getFolderBNames(new File(baseFolder, "_MALOS"));
        T_BNamesInfo goodUtils = getFolderBNames(new File(baseFolder, "_Buenos"));
        T_BNamesInfo goodGames = getFolderBNames(new File(baseFolder, "_GamesBuenos"));
        T_BNamesInfo toCheck = getFolderBNames(new File(baseFolder, "Disco_F"));

        _showWarnigIfIgnored(discarted, "Discarted");
        _showWarnigIfIgnored(goodUtils, "goodUtils");
        _showWarnigIfIgnored(goodGames, "goodGames");

        toCheck.removeAll(discarted);
        toCheck.removeAll(goodUtils);
        toCheck.removeAll(goodGames);

        System.out.println("\n\n*** Bundles to be processed");
        for (String name : toCheck.getToProcessBNames()) {
            System.out.println("  " + name);
        }

        _showWarnigIfIgnored(toCheck, "toCheck");
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt3(String[] args) throws Exception {

        File baseFolder = new File("W:\\iphone\\_FINISHED_\\crk-apps");

        T_BNamesInfo discarted = getFolderBNames(new File(baseFolder, "_MALOS"));
        T_BNamesInfo goodUtils = getFolderBNames(new File(baseFolder, "_Buenos"));
        T_BNamesInfo goodGames = getFolderBNames(new File(baseFolder, "_GamesBuenos"));

        _showWarnigIfIgnored(discarted, "Discarted");
        _showWarnigIfIgnored(goodUtils, "goodUtils");
        _showWarnigIfIgnored(goodGames, "goodGames");

        T_BNamesInfo toCheck;

        toCheck = new T_BNamesInfo(goodUtils);
        toCheck.removeAll(discarted);
        _showWarnigIfIgnored(toCheck, "toCheck Good Utils");

        toCheck = new T_BNamesInfo(goodGames);
        toCheck.removeAll(discarted);
        _showWarnigIfIgnored(toCheck, "toCheck Good Games");

        toCheck = new T_BNamesInfo(goodUtils);
        toCheck.removeAll(goodGames);
        _showWarnigIfIgnored(toCheck, "toCheck Crossed");
    }

    public T_BNamesInfo getFolderBNames(File afolder) throws Exception {

        T_BNamesInfo namesInfo = new T_BNamesInfo();

        if (afolder == null || !afolder.exists() || !afolder.isDirectory()) {
            return namesInfo;
        }

        for (File afile : afolder.listFiles(new FileExtFilter(false, "ipa"))) {

            String bdName = afile.getName();
            namesInfo.addBundleName(bdName);

        }

        File ipaBundleNames = new File(afolder, IPA_BUNDLE_NAMES_FILE);
        T_BNamesInfo fileBNInfo = _parseBNamesFile(ipaBundleNames);
        namesInfo.addAll(fileBNInfo);

        return namesInfo;

    }

    private T_BNamesInfo _parseBNamesFile(File afile) throws Exception {

        T_BNamesInfo namesInfo = new T_BNamesInfo();

        if (afile == null || !afile.exists() || !afile.isFile()) {
            return namesInfo;
        }

        BufferedReader br = new BufferedReader(new FileReader(afile));
        while (br.ready()) {

            String bdName = br.readLine();

            if (bdName == null || bdName.startsWith("#") || bdName.trim().length() == 0)
                continue;

            namesInfo.addBundleName(bdName);

        }
        br.close();

        return namesInfo;

    }

    private void _showWarnigIfIgnored(T_BNamesInfo tbni, String label) {
        Collection<String> ignored = tbni.getToIgnoreBNames();
        if (ignored.size() > 0) {
            System.out.println("\n\n*** WARNING: There are bundles to be ignored in '" + label + "'");
            for (String name : ignored) {
                System.out.println("  " + name);
            }
        }
    }
}
