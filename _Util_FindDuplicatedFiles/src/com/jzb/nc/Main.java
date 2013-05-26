/**
 * 
 */
package com.jzb.nc;

import java.io.File;

/**
 * @author n63636
 * 
 */
public class Main {

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
            Main me = new Main();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(0);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    private volatile ProcessFiles m_pf;
    private volatile long         m_t1;
    private volatile int          m_count;

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        // ********************************************************************************
        // ********************************************************************************
        final File baseFolders[] = { new File("/Users/jzarzuela/Documents/personal/Viajes/_Fotos_/__SIN COLOCAR__") 
        };
        // ********************************************************************************
        // ********************************************************************************

        final File outputFolder = new File("/Users/jzarzuela/Documents/java-Campus/_Util_FindDuplicatedFiles/out");
        outputFolder.mkdirs();

        m_pf = new ProcessFiles();

        new Thread(new Runnable() {

            @SuppressWarnings({ "synthetic-access" })
            public void run() {
                m_t1 = System.currentTimeMillis();
                System.out.println("*** Counting files");
                for (File baseFolder : baseFolders) {
                    _iterateFolder(baseFolder);
                }
                m_pf.setFileCount(m_count);
                System.out.println();
                System.out.println("*** Files counted: " + m_count);
                System.out.println();
            }
        }, "CountFiles").start();

        m_pf.processFiles(baseFolders, outputFolder);

        System.out.println("Enter some text to end the processing");
        System.in.read();
        m_pf.stop();

    }

    private void _iterateFolder(File folder) {

        System.out.println("--" + folder);
        long t2 = System.currentTimeMillis();
        if (t2 - m_t1 >= 5000) {
            m_t1 = t2;
            m_pf.setFileCount(m_count);
        }

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _iterateFolder(f);
            } else {
                m_count++;
            }
        }

    }
}
