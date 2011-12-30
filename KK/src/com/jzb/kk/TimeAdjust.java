/**
 * 
 */
package com.jzb.kk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

/**
 * @author n63636
 * 
 */
public class TimeAdjust {

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
            TimeAdjust me = new TimeAdjust();
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

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\n63636\\Desktop\\X10\\mycalendar.ics"));
        PrintWriter pw = new PrintWriter(new File("C:\\Users\\n63636\\Desktop\\X10\\mycalendar_NEW.ics"));
        while (br.ready()) {

            String s = br.readLine();
            s = _adjustTimezone(s);
            pw.println(s);
        }
        br.close();
        pw.close();
    }

    
    private String _adjustTimezone(String s) {
        int p = s.indexOf(";TZID=America/Los_Angeles:");
        if (p > 0) {
            String v = s.substring(0, p) + _adjustTime(s.substring(p + 25));
            return v;
        } else {
            return s;
        }
    }

    private String _adjustTime(String s) {

        int i = 2+Integer.parseInt(s.substring(10, 12));

        String v = s.substring(0, 10) + (i < 10 ? "0" : "") + Integer.toString(i) + s.substring(12);

        return v;

    }
}
