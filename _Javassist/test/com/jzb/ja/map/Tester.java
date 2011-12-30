/**
 * 
 */
package com.jzb.ja.map;

import java.util.HashMap;

import com.jzb.ja.map.data.DataFactory;

/**
 * @author n63636
 * 
 */
public class Tester {

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
            Tester me = new Tester();
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

        MapData[] mappings = DataFactory.createMapper();
        HashMap dataIn = DataFactory.createDataIn();
        HashMap dataOut = DataFactory.createDataOut();

        IntMapper mapper1 = new IntMapper(mappings);
        CompMapper mapper2 = new CompMapper(mappings);

        int MAX = 10000000;
        long t1, t2, t3, t4;

        //------------------------------------------------------
        t1 = System.currentTimeMillis();
        for (int n = 0; n < MAX; n++) {
            //mapper1.map(dataIn, dataOut);
        }
        t2 = System.currentTimeMillis();
        System.out.println("MAP1 FINISHED [" + (t2 - t1) + "]");
        
        //------------------------------------------------------
        t3 = System.currentTimeMillis();
        for (int n = 0; n < MAX; n++) {
            mapper2.map(dataIn, dataOut);
        }
        t4 = System.currentTimeMillis();
        System.out.println("MAP1 FINISHED [" + (t4 - t3) + "]");

        System.out.println("RATIO: "+(double)(t2-t1)/(double)(t4-t3));
    }
}
