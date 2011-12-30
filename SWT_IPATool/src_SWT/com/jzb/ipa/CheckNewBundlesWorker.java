/**
 * 
 */
package com.jzb.ipa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

import com.jzb.ipa.chk.T_BNamesInfo;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Tracer;
import com.jzb.futil.FileExtFilter;

/**
 * @author n000013
 * 
 */
public class CheckNewBundlesWorker extends BaseWorker {

    private static final String APPS_FOLDER           = "_apps";
    private static final String DISCARDED_FOLDER      = "_discarded";
    private static final String GAMES_FOLDER          = "_games";
    private static final String IPA_BUNDLE_NAMES_FILE = "ipaBundle.names";
    private static final String TOBECHECKED_FOLDER    = "_toBeChecked";

    public CheckNewBundlesWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void check(final String baseFolderStr) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("** Checking if new bundles were already processes");
                Tracer._info("");
                _check(new File(baseFolderStr));
                Tracer._info("");
                Tracer._info("** Checking done.");
                return null;
            }
        };

        _makeCall(baseFolderStr, callable);
    }

    private void _check(final File baseFolder) throws Exception {

        _usage();

        Tracer._debug("Reading bundles info for: " + DISCARDED_FOLDER);
        T_BNamesInfo discarted = _getFolderBNames(new File(baseFolder, DISCARDED_FOLDER));
        _showWarnigIfIgnored(discarted, DISCARDED_FOLDER);

        Tracer._debug("Reading bundles info for: " + APPS_FOLDER);
        T_BNamesInfo goodApps = _getFolderBNames(new File(baseFolder, APPS_FOLDER));
        _showWarnigIfIgnored(goodApps, APPS_FOLDER);

        Tracer._debug("Reading bundles info for: " + GAMES_FOLDER);
        T_BNamesInfo goodGames = _getFolderBNames(new File(baseFolder, GAMES_FOLDER));
        _showWarnigIfIgnored(goodGames, GAMES_FOLDER);

        Tracer._debug("Reading bundles info for: " + TOBECHECKED_FOLDER);
        T_BNamesInfo toBeCheck = _getFolderBNames(new File(baseFolder, TOBECHECKED_FOLDER));

        Tracer._debug("Cross checking " + APPS_FOLDER);
        T_BNamesInfo crossCheck;
        crossCheck = new T_BNamesInfo(goodApps);
        crossCheck.removeAll(discarted);
        crossCheck.removeAll(goodGames);
        _showWarnigIfIgnored(crossCheck, "Cross check in " + APPS_FOLDER);

        Tracer._debug("Cross checking " + GAMES_FOLDER);
        crossCheck = new T_BNamesInfo(goodGames);
        crossCheck.removeAll(discarted);
        crossCheck.removeAll(goodApps);
        _showWarnigIfIgnored(crossCheck, "Cross check in " + GAMES_FOLDER);

        Tracer._debug("Removing already processes bundles");
        toBeCheck.removeAll(discarted);
        toBeCheck.removeAll(goodApps);
        toBeCheck.removeAll(goodGames);
        _showWarnigIfIgnored(toBeCheck, TOBECHECKED_FOLDER);

        Tracer._debug("Checking for new versions in " + APPS_FOLDER);
        crossCheck = new T_BNamesInfo(goodApps);
        crossCheck.clearToBeIgnored();
        crossCheck.removeAll(toBeCheck);
        ArrayList<String> al1 = toBeCheck.getEquivalents(crossCheck.getToIgnoreBNames());
        _showWarnigIfIgnored(al1, "There are new versions for bundles in " + APPS_FOLDER);

        Tracer._debug("Checking for new versions in " + GAMES_FOLDER);
        crossCheck = new T_BNamesInfo(goodGames);
        crossCheck.clearToBeIgnored();
        crossCheck.removeAll(toBeCheck);
        ArrayList<String> al2 = toBeCheck.getEquivalents(crossCheck.getToIgnoreBNames());
        _showWarnigIfIgnored(al2, "There are new versions for bundles in " + GAMES_FOLDER);

        Tracer._debug("Checking for new versions in " + DISCARDED_FOLDER);
        crossCheck = new T_BNamesInfo(discarted);
        crossCheck.clearToBeIgnored();
        crossCheck.removeAll(toBeCheck);
        ArrayList<String> al3 = toBeCheck.getEquivalents(crossCheck.getToIgnoreBNames());
        _showWarnigIfIgnored(al3, "There are new versions for bundles in " + DISCARDED_FOLDER);
    }

    private T_BNamesInfo _getFolderBNames(File afolder) throws Exception {

        T_BNamesInfo namesInfo = new T_BNamesInfo();

        if (afolder == null || !afolder.exists() || !afolder.isDirectory()) {
            throw new Exception("Error: Invalid folder: " + afolder);
        }

        for (File afile : afolder.listFiles(new FileExtFilter(true, "ipa"))) {

            if (afile.isDirectory()) {
                namesInfo.addAll(_getFolderBNames(afile));
            } else {
                String bdName = afile.getName();
                namesInfo.addBundleName(bdName);
            }
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

    private void _showWarnigIfIgnored(Collection<String> ignored, String label) {
        if (ignored.size() > 0) {
            Tracer._warn("");
            Tracer._warn("*** WARNING: There are bundles to be ignored in '" + label + "'");
            Tracer._warn("");
            for (String name : ignored) {
                Tracer._warn("  " + name);
            }
        }
    }

    private void _showWarnigIfIgnored(T_BNamesInfo tbni, String label) {
        _showWarnigIfIgnored(tbni.getToIgnoreBNames(), label);
    }

    private void _usage() {
        Tracer._info("");
        Tracer._info("  ** Following sub-folders will be used:");
        Tracer._info("  *  " + DISCARDED_FOLDER);
        Tracer._info("  *  " + APPS_FOLDER);
        Tracer._info("  *  " + GAMES_FOLDER);
        Tracer._info("  *  " + TOBECHECKED_FOLDER);
        Tracer._info("  *");
        Tracer._info("  ** Bundle Names file: " + IPA_BUNDLE_NAMES_FILE);
        Tracer._info("");

    }

}
