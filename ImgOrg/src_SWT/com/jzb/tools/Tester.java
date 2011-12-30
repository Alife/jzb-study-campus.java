/**
 * 
 */
package com.jzb.tools;

import java.io.InputStream;
import java.util.ArrayList;

import com.jzb.tools.act.FlickrContext;
import com.jzb.tools.xmlbean.ActTaskManager;
import com.jzb.tools.xmlbean.IActTask;
import com.jzb.tools.xmlbean.IActTaskManager;
import com.jzb.tools.xmlbean.SysOutTracer;
import com.jzb.tools.xmlbean.XMLActParser;

/**
 * @author n000013
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

        FlickrContext.init();

        InputStream is = this.getClass().getResourceAsStream("/Data.xml");
        ArrayList<IActTask> atList = XMLActParser.parse(new SysOutTracer(), is);

        IActTaskManager manager = new ActTaskManager("prueba", new SysOutTracer(), null, 10);
        for (IActTask actTask : atList) {
            actTask.prepareExecution(null, null);
            manager.submitActTask(actTask);
        }
        manager.waitForFinishing();

    }

}
