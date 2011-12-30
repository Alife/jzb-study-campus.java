/**
 * 
 */
package com.jzb.ipa.ssh;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class SSHIPAInstaller_OLD {

    private class MyFTPMonitor implements SftpProgressMonitor {

        private IInstMonitor m_instMonitor;
        private long         m_sent;
        private long         m_size;

        public MyFTPMonitor(long size, IInstMonitor instMonitor) {
            m_size = size;
            m_instMonitor = instMonitor;
        }

        public boolean count(long count) {
            m_sent += count;
            if (m_instMonitor != null) {
                int percentage = (int) (100L * m_sent / m_size);
                m_instMonitor.sendFileProgress(percentage);
            }
            return true;
        }

        public void end() {
        }

        public IInstMonitor getIInstMonitor() {
            return m_instMonitor;
        }

        public void init(int op, String src, String dest, long max) {
        }

    }

    public static int           TIMEOUT     = 10000;

    private static final String RINSTFOLDER = "/tmp/inst2";

    private static int          s_uidCount  = 0;
    private ChannelSftp         m_ftpChnl;
    private Session             m_session;

    public SSHIPAInstaller_OLD() {
    }

    public void connect(String host, String usr, String pwd) throws Exception {

        _createSession(host, usr, pwd);
        _connectFTP(m_session);
        _checkInstTmpFolder(m_ftpChnl, RINSTFOLDER);

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

    public InputStream getRemoteFile(String fname) throws Exception {

        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        
        Tracer._debug("+ getting remote file begins for: " + fname);
        m_ftpChnl.get(fname, baos);
        Tracer._debug("- getting remote file done");
        
        baos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return bais;
    }
    
    public boolean installIPABundle(File ipaFile, IInstMonitor instMonitor) {

        if (instMonitor == null)
            instMonitor = IInstMonitor.nullMonitor;

        try {
            if (!ipaFile.exists()) {
                throw new Exception("Error, file doesn't exist: " + ipaFile);
            }

            instMonitor.processBegin();
            Tracer._debug("+ IPA bundle installation begins");

            // Cleaning any previous installation remains
            _execCommand(m_session, "rm -f -r " + RINSTFOLDER + "/*");
            Thread.sleep(150);

            // -------- SEND IPA FILE
            String rname = RINSTFOLDER + "/" + ipaFile.getName();
            _sendFile(ipaFile, rname, instMonitor);
            Thread.sleep(150);

            // -------- SEND installation script
            String rscript = RINSTFOLDER + "/instScript";
            InputStream is = SSHIPAInstaller_OLD.class.getResourceAsStream("/instScript");
            _sendFileIS(is, rscript, null);
            Thread.sleep(150);
            _execCommand(m_session, "chmod 0777 " + rscript);
            Thread.sleep(150);

            // --------- PROCESS FILE
            instMonitor.installingBundle();
            String appFolder = _generateUIDName();
            _execCommand(m_session, rscript + " \"" + rname + "\" " + appFolder);
            Thread.sleep(150);

            instMonitor.processEnd(false);
            Tracer._debug("- IPA bundle installation done");
            return true;

        } catch (Throwable th) {
            instMonitor.processEnd(true);
            Tracer._error("* IPA bundle installation Failed: " + ipaFile);
            return false;
        }
    }

    public void resetSpringBoard() throws Exception {
        _execCommand(m_session, "killall QUIT SpringBoard");
    }

    private String _adjustHexString(String s, int len) {

        if (s.length() > len) {
            s = s.substring(s.length() - len);
        } else if (s.length() < len) {
            int l = len - s.length();
            for (int n = 0; n < l; n++) {
                s = "0" + s;
            }
        }

        return s;
    }

    private void _checkInstTmpFolder(ChannelSftp c, String rfolder) throws Exception {

        Tracer._debug("+ Ensuring that tmp installation folder '" + rfolder + "' exists");
        try {
            c.mkdir(rfolder);
        } catch (SftpException e) {
        }
        c.cd(rfolder);
        Tracer._debug("- Installation tmp folder exists");
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

    private String _execCommand(Session session, String cmd) throws Exception {

        Tracer._debug("+ Executing remote command: " + cmd);

        ChannelExec ce = (ChannelExec) session.openChannel("exec");
        ce.setCommand(cmd);
        // OutputStream out = ce.getOutputStream();
        InputStream in = ce.getInputStream();
        ce.connect(30000);

        StringBuffer sb = new StringBuffer();
        StringBuffer trz = new StringBuffer();

        while (!ce.isEOF()) {
            while (in.available() > 0) {
                int i = in.read();
                sb.append((char) i);
                if (i != 10 && i != 1)
                    trz.append((char) i);
                if (i == 10 || i == 13) {
                    Tracer._debug("SSH OUT: " + trz.toString());
                    trz = new StringBuffer();
                }
            }
        }

        int r = ce.getExitStatus();

        ce.disconnect();

        if (r != 0) {
            throw new Exception("ERROR executing remote command. Result code: " + r + ", cmd: " + cmd);
        }

        Tracer._debug("- Executing remote command done");

        return sb.toString();
    }

    private String _generateUIDName() {

        s_uidCount = (s_uidCount + 1) % 0xFFFF;

        String s1 = Long.toHexString(s_uidCount).toUpperCase();
        String s2 = Long.toHexString(System.currentTimeMillis()).toUpperCase();

        String UID = "00000000-BEBE-CABE-" + _adjustHexString(s1, 4) + "-" + _adjustHexString(s2, 12);

        return UID;

    }

    private void _sendFile(File localFile, String remoteName, IInstMonitor instMonitor) throws Exception {

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFile), 65536);
        MyFTPMonitor ftpMonitor = new MyFTPMonitor(localFile.length(), instMonitor);
        _sendFileIS(bis, remoteName, ftpMonitor);
        bis.close();

    }

    private void _sendFileIS(InputStream is, String remoteName, MyFTPMonitor instMonitor) throws Exception {

        Tracer._debug("+ Sending file begins for: " + remoteName);

        if (instMonitor != null && instMonitor.getIInstMonitor() != null) {
            instMonitor.getIInstMonitor().sendFileBegin();
        }

        m_ftpChnl.put(is, remoteName, instMonitor, ChannelSftp.OVERWRITE);

        if (instMonitor != null && instMonitor.getIInstMonitor() != null) {
            instMonitor.getIInstMonitor().sendFileEnd();
        }

        Tracer._debug("- Sending file done");
    }
}
