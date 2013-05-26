/**
 * 
 */
package com.jzb.nio;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author jzarzuela
 * 
 */
public class NIOTest {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            NIOTest me = new NIOTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
            th.printStackTrace(System.out);
            System.exit(-1);
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

        File baseFolder = new File("/Users/jzarzuela/Documents/_TMP_/test/__done__/Familia/2005_10_08-Roma");
        _readAllFiles(baseFolder);
    }

    private void _readAllFiles(File folder) throws Exception {

        File allfiles[] = folder.listFiles();
        if (allfiles != null) {
            for (File f : allfiles) {
                if (f.isDirectory())
                    _readAllFiles(f);
                else
                    _readFileContent1(f);
            }
        }
    }

    private static final int BUFFER_SIZE = 1024 * 64;
    private byte             m_buffer[]  = new byte[BUFFER_SIZE];
    private ByteBuffer       m_chBuffer  = ByteBuffer.allocate(BUFFER_SIZE);

    private void _readFileContent1(File fin) throws Exception {

        FileInputStream fis = new FileInputStream(fin);
        FileChannel fc = fis.getChannel();
        fis.skip(2000);
        for (;;) {
            int len = fc.read(m_chBuffer);
            if (len <= 0)
                break;
        }
        fis.close();
    }

    private void _readFileContent2(File fin) throws Exception {
        FileInputStream fis = new FileInputStream(fin);
        BufferedInputStream bis = new BufferedInputStream(fis);
        for (;;) {
            int len = bis.read(m_buffer);
            if (len <= 0)
                break;
        }
        bis.close();
        fis.close();
    }
}
