/**
 * 
 */
package com.jzb.flickr;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author n000013
 * 
 */
public class SSocket {

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
            SSocket me = new SSocket();
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

        FileOutputStream fos = new FileOutputStream("c:\\pp.out", false);
        fos.close();

        System.out.println("*** Create server socket at port 9999 ***");
        ServerSocket ss = new ServerSocket(9999);

        System.out.println("*** Wait for a request ***");
        Socket cs = ss.accept();

        cs.setSoTimeout(10000);
        System.out.println("*** Reading the request ***\n\n");
        InputStream is = cs.getInputStream();
        int timeout_counter = 10;
        byte buffer[] = new byte[2048];
        for (;;) {

            if (is.available() < 0) {
                if (timeout_counter <= 0)
                    break;

                Thread.sleep(50);
                timeout_counter--;
            } else {
                timeout_counter = 10;
                int i = is.read(buffer);
                if (i > 0) {
                    fos = new FileOutputStream("c:\\pp.out", true);
                    fos.write(buffer, 0, i);
                    fos.close();
                    System.out.print(new String(buffer,0,i));
                }
            }
        }

        System.out.println("\n\n*** Done with the request ***\n\n");
        is.close();
        cs.close();
        ss.close();
    }
}
