/**
 * 
 */
package com.jzb.nc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author n63636
 * 
 */
public class KK {

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
            KK me = new KK();
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

        Pattern p = Pattern.compile("move (.*)IMG_(.*)",Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
        
        File fin = new File("E:\\_Backup_\\_Fotos_\\_Fotos_\\__SIN COLOCAR__\\del.cmd");
        BufferedReader br = new BufferedReader(new FileReader(fin));
        while(br.ready()) {
            String cad = br.readLine();
            Matcher m = p.matcher(cad);
            if(m.matches()) {
                //System.out.println(cad);
                System.out.println("move \".\\kk\\IMG_"+m.group(2)+"   "+m.group(1)+"IMG_"+m.group(2));
            }
        }
        br.close();
        
    
    }
    
    
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt2(String[] args) throws Exception {

        File folder = new File("E:\\_Backup_\\_Fotos_\\_Fotos_\\__SIN COLOCAR__\\Boston");
        processFolder(folder);
        System.out.println();
        System.out.println(minLM+" - "+sdf.format(new Date(minLM)));
        System.out.println(maxLM+" - "+sdf.format(new Date(maxLM)));
    }

    private long minLM = Long.MAX_VALUE;
    private long maxLM = Long.MIN_VALUE;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
    private void processFolder(File folder) throws Exception {

        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                processFolder(f);
            } else {
                long lm = f.lastModified();
                System.out.println(sdf.format(new Date(lm))+" - "+f.getName());
                if (1900 + new Date(lm).getYear() < 2011) {
                    if (minLM > lm)
                        minLM = lm;
                    if (maxLM < lm)
                        maxLM = lm;
                }
            }
        }
    }
}
