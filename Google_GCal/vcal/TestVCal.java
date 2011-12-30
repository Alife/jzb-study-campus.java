

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.VCalendar;

/**
 * @author n000013
 * 
 */
public class TestVCal {

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
            TestVCal me = new TestVCal();
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

        //URI.create("CN=EVA CORDOBES MARTINEZ/O=BANESTO");
        CalendarBuilder cb=new CalendarBuilder();
        VCalendar cal=cb.build(new FileInputStream("C:\\Program Files\\Lotus\\Notes\\data\\iCalendar\\Invitation.ics"));
        System.out.println(cal.getComponents());
    }
}
