/**
 * 
 */
package com.jzb.kk;

import java.io.BufferedInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author n63636
 * 
 */
public class SS {

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
            SS me = new SS();
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
        ServerSocket ss = new ServerSocket(9090);
        Socket cs=ss.accept();
        BufferedInputStream bif=new BufferedInputStream(cs.getInputStream());
        Thread.sleep(50);
        while(bif.available()>0) {
            int n=bif.read();
            System.out.print((char)(byte)n);
        }
        bif.close();
    }
}
