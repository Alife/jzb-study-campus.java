/**
 * 
 */


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class Test1 {

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
            Test1 me = new Test1();
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

        System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "trace");

        WebClient wclient = new WebClient(BrowserVersion.FIREFOX_3, "cache.sscc.banesto.es", 8080);
        wclient.setJavaScriptEnabled(false);
        HtmlPage page = wclient.getPage("http://apptrackr.org");
        Tracer._debug(page.getTitleText());
        Tracer._debug("");
        Tracer._debug(page.asXml());
    }
}
