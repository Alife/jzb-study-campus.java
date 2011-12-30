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
public class ParseICS {

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
            ParseICS me = new ParseICS();
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

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\n63636\\Desktop\\X10\\mycalendar_NEW.ics"));
        boolean inEvent = false;
        String summary = "";
        String sdate = "";
        String edate = "";
        while (br.ready()) {

            String s = br.readLine();

            if (s.startsWith("BEGIN:VEVENT")) {
                inEvent = true;
                continue;
            }

            if (inEvent && s.startsWith("SUMMARY:")) {
                int p = s.indexOf(" ", 8);
                if (p > 0)
                    summary = s.substring(p+1);
                else
                    summary = s.substring(8);
                continue;
            }

            if (inEvent && s.startsWith("DTSTART:")) {
                sdate = s.substring(8);
                continue;
            }

            if (inEvent && s.startsWith("DTEND:")) {
                edate = s.substring(6);
                continue;
            }

            if (s.startsWith("END:VEVENT")) {
                System.out.println(sdate + ", " + edate + ", " + summary);
                summary = sdate = edate = "";
                inEvent = false;
                continue;
            }
        }
        br.close();

    }
}
