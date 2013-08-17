/**
 * 
 */
package com.jzb.nc;

import java.io.File;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author n63636
 * 
 */
public class ProcessFiles {

    private static final char[] hex         = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', };

    // private transient int m_baseFolderLen;
    private byte                m_buffer[]  = new byte[3072];
    private MessageDigest       m_md5;
    private transient File      m_outFolder;
    private transient boolean   m_stop;
    private Thread              m_thread;

    private int                 m_fileCount = 0;
    private int                 m_count     = 0;
    private long                m_startTime = 0;
    private long                m_initTime = 0;

    public ProcessFiles(long initTime) {
        m_initTime = initTime;
    }
    
    public void processFiles(final File baseFolders[], final File outFolder) throws Exception {

        m_thread = new Thread(new Runnable() {

            @SuppressWarnings("synthetic-access")
            public void run() {
                try {
                    _processFiles(baseFolders, outFolder);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        m_thread.start();

    }

    public synchronized void setFileCount(int count) {
        m_fileCount = count;
    }

    public synchronized int getFileCount() {
        return m_fileCount;
    }

    public void stop() throws Exception {
        synchronized (this) {
            m_stop = true;
        }
        m_thread.join();
    }

    private String _calcHash(File f, byte buffer[], int len) {

        byte length[] = new byte[8];
        long l = f.length();
        for (int n = 0; n < 8; n++) {
            length[n] = (byte) (l & 0x0FF);
            l = l >> 8;
        }

        m_md5.reset();
        m_md5.update(length);
        m_md5.update(buffer, 0, len);
        return _toHex(m_md5.digest());

    }

    private boolean _hasToStop() {
        synchronized (this) {
            return m_stop;
        }
    }

    private String _getProgressText() {

        int fileCount = getFileCount();

        m_count++;

        String str = "[" + m_count + "/" + fileCount;

        if (fileCount > 0 && m_count > 0) {

            long timeLeft = ((System.currentTimeMillis() - m_startTime) * (fileCount - m_count)) / m_count;

            long sec = timeLeft / 1000;
            if (timeLeft > 60000) {
                long min = sec / 60;
                sec = sec - min * 60;
                str += " - " + min + "m " + sec + "s";
            } else {
                str += " - " + sec + "s";
            }
        }

        str += "]";

        return str;
    }

    private String _processFile(File f) {

        try {
            int len = 0;
            RandomAccessFile raf = new RandomAccessFile(f, "r");
            if (f.length() < 3072) {
                len = raf.read(m_buffer);
            } else {
                long pos1 = f.length() / 2 - 512;
                long pos2 = f.length() - 1024;

                len += raf.read(m_buffer, 0, 1024);
                raf.seek(pos1);
                len += raf.read(m_buffer, 1024, 1024);
                raf.seek(pos2);
                len += raf.read(m_buffer, 2048, 1024);

                if (len != 3072) {
                    throw new Exception("Not enought bytes (" + len + ") read from file '" + f + "'");
                }
            }
            raf.close();

            if (len > 0)
                return _calcHash(f, m_buffer, len);
            else
                return "**UNKNOWN HASH**";
        } catch (Throwable th) {
            return "**Error Hashing: " + th.getMessage();
        }
    }

    private void _processFiles(File baseFolders[], File outFolder) throws Exception {

        m_startTime = System.currentTimeMillis();
        m_count = 0;

        System.out.println("*************** STARTED ***************");
        m_md5 = MessageDigest.getInstance("MD5");

        m_outFolder = outFolder;

        // m_baseFolderLen = baseFolder.getAbsolutePath().length();

        m_stop = false;
        for (File baseFolder : baseFolders) {
            _processFolder(baseFolder);
        }

        System.out.println("*************** ENDED ***************");
        System.err.println("-- ya --");
        long t2 = System.currentTimeMillis();
        System.out.println("***** TEST FINISHED [" + (t2 - m_initTime) + "]*****");
        System.exit(1);
    }

    private void _processFolder(File folder) throws Exception {

        System.out.println("Processing Folder: " + folder);

        // ***** STOPPING CONDITION *****
        if (_hasToStop()) {
            return;
        }

        File outFile = getOutFile(folder);
        //if (!outFile.exists()) 
        {

            // First we process folders then files
            File lfiles[] = folder.listFiles();
            if (lfiles != null) {
                for (File f : lfiles) {

                    // ***** STOPPING CONDITION *****
                    if (_hasToStop())
                        return;

                    if (f.isDirectory() && !_skipFolderProcessing(f)) {
                        _processFolder(f);
                    }
                }
            }

            ArrayList<String> fileData = new ArrayList<String>();

            if (lfiles != null) {
                for (File f : lfiles) {

                    // ***** STOPPING CONDITION *****
                    if (_hasToStop())
                        return;

                    if (!f.isDirectory() && !_skipFileProcessing(f)) {
                        System.out.println("   " + _getProgressText() + " - Processing file: " + f);
                        fileData.add(f.getAbsolutePath());
                        String hash = _processFile(f);
                        fileData.add(hash);
                    }
                }
            }
            saveOutFile(outFile, fileData);
            fileData = null;

        }
    }

    private boolean _skipFolderProcessing(File f) throws Exception {
        if(f.getAbsolutePath().contains("/.git")) {
            return true;
        }
        return false;
    }

    private boolean _skipFileProcessing(File f) throws Exception {
        return false;
    }
    
    private String _toHex(byte hash[]) {
        StringBuffer buf = new StringBuffer(hash.length * 2);

        for (int idx = 0; idx < hash.length; idx++)
            buf.append(hex[(hash[idx] >> 4) & 0x0f]).append(hex[hash[idx] & 0x0f]);

        return buf.toString();
    }

    private File getOutFile(File folder) {
        /*
         * String relativePath = folder.getAbsolutePath().substring(m_baseFolderLen).replace('\\', '#'); if (relativePath.length() == 0) { relativePath = "_root_"; }
         */
        String relativePath = folder.getAbsolutePath().replace(File.separatorChar, '#').replace(':', '#');
        File outFile = new File(m_outFolder, relativePath + "_out.txt");
        return outFile;
    }

    private void saveOutFile(File outFile, ArrayList<String> fileData) throws Exception {
        try {
            PrintWriter pw;
            pw = new PrintWriter(outFile);
            Iterator<String> iter = fileData.iterator();
            while (iter.hasNext()) {
                pw.print(iter.next());
                pw.print(", ");
                pw.println(iter.next());
            }
            pw.close();
        } catch (Exception ex) {
            outFile.delete();
            throw ex;
        }
    }

}
