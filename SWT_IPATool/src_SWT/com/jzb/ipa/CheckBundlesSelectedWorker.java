/**
 * 
 */
package com.jzb.ipa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.jzb.ipa.bundle.BinaryPListParser;
import com.jzb.ipa.ssh.SSHIPAInstaller;
import com.jzb.swt.util.BaseWorker;

import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;
import com.jzb.futil.FileExtFilter;

/**
 * @author n000013
 * 
 */
public class CheckBundlesSelectedWorker extends BaseWorker {

    public CheckBundlesSelectedWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void check(final String baseFolderStr) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("** Checking which bundles were selected");
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

        _getIPABundlesInfo(baseFolder);

        ArrayList<String> selected = _getRemoteNameList("/var/mobile/Library/Preferences/com.bigboss.categories.Selected.plist");
        ArrayList<String> discarded = _getRemoteNameList("/var/mobile/Library/Preferences/com.bigboss.categories.Discarded.plist");

        PrintWriter pw = new PrintWriter(new File(baseFolder, "moveSelected.cmd"));
        pw.println("@echo off");
        pw.println("mkdir _selected");
        pw.println("mkdir _discarded");

        Tracer._debug("\n\n** Selected bundles:\n\n");
        for (String name : selected) {
            File f = m_bundlesInfo.remove(name);
            if (f != null) {
                Tracer._debug("  " + f.getName());
                String nameWOExt = f.getName().substring(0, f.getName().length() - 3);
                pw.println("move \"" + nameWOExt + "*\" _selected\\.");
            } else {
                Tracer._error("Installed-Selected app Bundle not found for: " + name);
            }
        }

        Tracer._debug("\n\n** Discarded bundles:\n\n");
        for (String name : discarded) {
            File f = m_bundlesInfo.remove(name);
            if (f != null) {
                Tracer._debug("  " + f.getName());
                String nameWOExt = f.getName().substring(0, f.getName().length() - 3);
                pw.println("move \"" + nameWOExt + "*\" _discarded\\.");
            } else {
                Tracer._error("Installed-Discarded app Bundle not found for: " + name);
            }
        }

        Tracer._debug("\n\n** NON INSTALLED bundles:\n\n");
        for (File f : m_bundlesInfo.values()) {
            Tracer._debug("  " + f.getName());
        }

        pw.println("pause");
        pw.close();
    }

    private ArrayList<String> _getRemoteNameList(String remoteFile) throws Exception {

        ArrayList<String> names = new ArrayList<String>();

        SSHIPAInstaller ssh = new SSHIPAInstaller();
        ssh.connect("127.0.0.1", Des3Encrypter.decryptStr("i84ommBJaBQ="), Des3Encrypter.decryptStr("WvO3H3yRW2nPsNpQjcGPFA=="));
        InputStream is = ssh.getRemoteFile(remoteFile);
        File tmp = File.createTempFile("ipatool-", ".tmp");
        FileOutputStream fos = new FileOutputStream(tmp);
        byte buffer[] = new byte[4096];
        while (is.available() > 0) {
            int len = is.read(buffer);
            if (len <= 0)
                break;
            fos.write(buffer, 0, len);
        }
        is.close();
        fos.close();
        ssh.disconnect();

        BinaryPListParser bplp = new BinaryPListParser();
        Document xml = bplp.parse(tmp);
        NodeList nl = xml.getElementsByTagName("string");
        for (int n = 0; n < nl.getLength(); n++) {
            names.add(nl.item(n).getTextContent());
        }

        tmp.delete();

        return names;
    }

    private HashMap<String, File> m_bundlesInfo = new HashMap<String, File>();

    private void _getIPABundlesInfo(File baseFolder) throws Exception {

        Tracer._debug("Reading bundles info from: " + baseFolder);
        for (File afile : baseFolder.listFiles(new FileExtFilter(false, "ipa"))) {
            String pkgName = _getBNameElement(afile.getName(),"PK"); 
            m_bundlesInfo.put(pkgName, afile);

        }
    }

    private String _getBNameElement(String name, String part) {

        String startStr = "_" + part + "[";
        int p1 = name.indexOf(startStr);
        if (p1 < 0)
            return null;

        int p2 = name.indexOf("]", p1);
        if (p2 < 0)
            return null;

        return name.substring(p1 + startStr.length(), p2);
    }

}
