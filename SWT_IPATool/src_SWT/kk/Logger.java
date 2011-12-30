/**
 * 
 */
package kk;


/**
 * @author n63636
 *
 */
public class Logger {
    public Logger() {
    }

    public void debug(String s) {
        System.out.println(s);
    }

    public void error(String s) {
        System.out.println(s);
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public void warn(String s) {
        System.out.println(s);
    }

}
