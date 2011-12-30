/**
 * 
 */
package com.jzb.ser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;


/**
 * @author PS00A501
 * 
 */
public class _Tester {

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
            _Tester me = new _Tester();
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
        DataInputStream dis = new DataInputStream(new FileInputStream("D:/WKSPs/WS33_VACIO/Booklet/res/data/bin.data"));
        String name=dis.readUTF();
        int len = dis.readInt();
        byte buffer[]=new byte[len];
        dis.read(buffer);
        dis.close();

        dis = new DataInputStream(new ByteArrayInputStream(buffer));
        MenuItem root= MenuItem.createRoot();
        root.readExternal(dis);
        dis.close();

    }
}
