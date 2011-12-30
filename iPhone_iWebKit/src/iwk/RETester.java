/**
 * 
 */
package iwk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n63636
 * 
 */
public class RETester {

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
            RETester me = new RETester();
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

        Pattern p=Pattern.compile(".*\\.(png|jpg)",Pattern.CASE_INSENSITIVE);
        Matcher m=p.matcher("pepe.gif");
        System.out.println(m.matches());
    }
}
