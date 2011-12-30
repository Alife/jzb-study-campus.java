/**
 * 
 */
package com.jzb.tt.sim;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author n63636
 * 
 */
public class SimServer {

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
            SimServer me = new SimServer();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
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

        ServerSocket ss = new ServerSocket(8080);
        System.out.println("Waiting for Socket connection at port 8080");
        Socket cs = ss.accept();
        System.out.println("Socket accepted");

        System.out.println("Reading data");
        System.out.println("-------------------------------------------------------------------");
        InputStream is = cs.getInputStream();
        for (;;) {
            Thread.sleep(100);
            byte[] buffer = new byte[100000];
            int len = is.read(buffer);
            for (int n = 0; n < len; n++) {
                System.out.print((char)buffer[n]);
            }
            if (len < 0)
                break;
        }
        is.close();

        System.out.println("Connection ended");
    }
}
