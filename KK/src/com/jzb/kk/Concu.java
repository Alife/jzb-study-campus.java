/**
 * 
 */
package com.jzb.kk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author n63636
 * 
 */
public class Concu {

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
            Concu me = new Concu();
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

        Callable<Calendar> myCall = new Callable<Calendar>() {

            /**
             * @see java.util.concurrent.Callable#call()
             */
            @Override
            public Calendar call() throws Exception {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                System.out.println("Hola - " + Thread.currentThread().getName());
                Calendar cal = Calendar.getInstance();
                cal.set(10, 10, 10, 10, 10, 10);
                return cal;
            }
            
        };

        Calendar cal = Calendar.getInstance();
        cal.set(1, 1, 1, 1, 1, 1);
        System.out.println(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM-%1$tS ",cal));

        ExecutorService es = Executors.newCachedThreadPool();
        
        FutureTask ft1 = new FutureTask(myCall);
        es.execute(ft1);
        FutureTask ft2 = new FutureTask(myCall);
        es.execute(ft2);
        
        Object o1=ft1.get();
        System.out.println(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM-%1$tS ",o1));
        Object o2=ft2.get();
        System.out.println(String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM-%1$tS ",o2));
        
        es.shutdown();
        es.awaitTermination(2,TimeUnit.MINUTES);
    }
}
