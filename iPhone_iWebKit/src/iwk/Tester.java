/**
 * 
 */
package iwk;

import java.io.File;

import mmhttp.server.Server;

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

        File baseResFolder1=new File("./");
        File baseResFolder2=new File("./MyTest");
        File baseResFolders[]= {baseResFolder1,baseResFolder2};
        
        Server server = new Server(8002);
        server.register("hello.*", FileResponder.class);
        //server.register(".*/..*", new FileResponder(baseResFolders));
        //server.register(".*\\..*", new FileResponder(baseResFolders));
        server.start();
        System.in.read();
        server.stop();
    }
}
