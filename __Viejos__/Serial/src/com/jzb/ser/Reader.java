package com.jzb.ser;

public class Reader {

    public static void main(String[] args) {
        long t2, t1 = System.currentTimeMillis();
        try {
            System.out.println("************ STARTED ************");
            Reader me = new Reader();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("********** FINISHED[" + (t2 - t1) + "] **********");
        } catch (Exception e) {
            t2 = System.currentTimeMillis();
            System.out.println("**!!!!!!!!!! FAILED[" + (t2 - t1) + "] !!!!!!!!!!**");
            e.printStackTrace(System.out);
        }
        System.exit(0);
    }

    private static final String BASE_PATH = "D:/JZarzuela/utilidades/Booklet_Sync/Rcve_Data/";

    public void doIt(String[] args) throws Exception {

        SPortReader sr=new SPortReader();
        if (args.length == 0) {
            System.out.println("\n");
            System.out.println("Warning:");
            System.out.println("    No working base path for receiving was indicated. Using default value:");
            System.out.println("    " + BASE_PATH);
            System.out.println("\n\n");
            args = new String[] { BASE_PATH };
        }
        sr.receive(args[0], "COM5");
    }

}
