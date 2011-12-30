/**
 * 
 */
package kk;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jzb.ipa.bundle.BinaryBundleParser;
import com.jzb.ipa.bundle.BundleData;
import com.jzb.ipa.bundle.IBundleDataParser;
import com.jzb.ipa.bundle.XMLBundleParser;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class GetIPAs {

    public static int   TIMEOUT  = 10000;
    private static final String     IPA_NAME_PATTERN = "crk_N[%BN%]_PK[%BPKGID%]_V[%BV%]_OS[%BMOSV%]_D[%BTM%]";
    private static SimpleDateFormat s_sdf            = new SimpleDateFormat("yyyy-MM");

    private ChannelSftp m_ftpChnl;

    private Session     m_session;


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
            GetIPAs me = new GetIPAs();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    public void connect(String host, String usr, String pwd) throws Exception {

        _createSession(host, usr, pwd);
        _connectFTP(m_session);

    }

    public void disconnect() {

        try {
            Tracer._debug("+ Closing FTP channel");
            if (m_ftpChnl != null)
                m_ftpChnl.disconnect();
            m_ftpChnl = null;
            Tracer._debug("- Closed FTP channel");
        } catch (Throwable th) {
            Tracer._warn("Error closing FTP channel", th);
        }

        try {
            Tracer._debug("+ Disconnecting SSH session");
            if (m_session != null)
                m_session.disconnect();
            m_session = null;
            Tracer._debug("- Disconnected SSH session");
        } catch (Throwable th) {
            Tracer._warn("Errord isconnecting SSH session", th);
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

        connect("127.0.0.1", Des3Encrypter.decryptStr("i84ommBJaBQ="), Des3Encrypter.decryptStr("WvO3H3yRW2nPsNpQjcGPFA=="));
        listIPAPaths();
        disconnect();
    }

    private void _connectFTP(Session session) throws Exception {
        Tracer._debug("+ Opening FTP Channel");
        m_ftpChnl = (ChannelSftp) session.openChannel("sftp");
        m_ftpChnl.connect(TIMEOUT);
        Tracer._debug("- Opened FTP Channel");
    }
    private void _createSession(String host, String usr, String pwd) throws Exception {

        Tracer._debug("+ Creating SSH session");
        JSch jsch = new JSch();
        m_session = jsch.getSession(usr, host, 22);
        m_session.setPassword(pwd);
        m_session.setConfig("StrictHostKeyChecking", "no");
        Tracer._debug("- Created SSH session");

        Tracer._debug("+ Connecting SSH session");
        m_session.connect(TIMEOUT);
        Tracer._debug("- Conected SSH session");
    }

    private String _getNewBundleName(BundleData bdata) {

        String newName = IPA_NAME_PATTERN;

        newName = newName.replace("%BTM%", s_sdf.format(bdata.time));
        newName = newName.replace("%BN%", bdata.name);
        newName = newName.replace("%BV%", bdata.version);
        newName = newName.replace("%BMOSV%", bdata.minOSVersion);
        newName = newName.replace("%BPKGID%", bdata.pkgID);

        return newName;
    }

    private String getIPAInfoFile(String infoFile) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        m_ftpChnl.get(infoFile, baos);
        baos.close();
        byte buffer[] = baos.toByteArray();

        IBundleDataParser parser;
        if (buffer[0] == 98 && buffer[1] == 112 & buffer[2] == 108) {
            parser = new BinaryBundleParser();
        } else {
            parser = new XMLBundleParser();
        }
        BundleData bd = parser.parse(buffer);
        return _getNewBundleName(bd);
    }

    private void listIPAPaths() throws Exception {

        Vector vFolders, vFolders2;

        vFolders = m_ftpChnl.ls("/User/Applications");
        for (int n = 0; n < vFolders.size(); n++) {
            LsEntry entry = (LsEntry) vFolders.get(n);
            String name = entry.getFilename();
            if (entry.getAttrs().isDir() && !name.equals(".") && !name.equals("..")) {
                String baseFolder = "/User/Applications/" + name;
                vFolders2 = m_ftpChnl.ls(baseFolder);
                for (int i = 0; i < vFolders2.size(); i++) {
                    LsEntry entry2 = (LsEntry) vFolders2.get(i);
                    String name2 = entry2.getFilename();
                    if (entry2.getAttrs().isDir() && name2.endsWith(".app")) {
                        String nameCrk = getIPAInfoFile(baseFolder + "/" + name2 + "/Info.plist");
                        System.out.println("rm -f -r " + baseFolder + "  " +  nameCrk);
                    }
                }
            }
        }

        vFolders = m_ftpChnl.ls("/Applications");
        for (int n = 0; n < vFolders.size(); n++) {
            LsEntry entry = (LsEntry) vFolders.get(n);
            String name = entry.getFilename();
            if (entry.getAttrs().isDir() && name.endsWith(".app")) {
                String nameCrk = getIPAInfoFile("/Applications/" +  name + "/Info.plist");
                System.out.println("/Applications  " + nameCrk);
            }
        }
    }
}
