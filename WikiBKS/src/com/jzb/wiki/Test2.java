/**
 * 
 */
package com.jzb.wiki;

import java.io.PrintWriter;

import com.jzb.wiki.dt.TWikiItem;
import com.jzb.wiki.tkn.WikiParser;

/**
 * @author n63636
 * 
 */
public class Test2 {

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
            Test2 me = new Test2();
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

        WikiParser parser = new WikiParser();

        //TWikiItem doc = parser.parsePage("Estudios_estratégicos_Seguridad", true);
        TWikiItem doc = parser.parsePage("Acta_Comite_Seguimiento_Proyectos_21-12-2010", true);
        

        //String str = "{{plantilla ejemplo | param p1=hola | p2=adios | x={{anidada | k1 |k2}}| mas de lo mismo |menos}}{{pepe | {{anidada |kx}} }}";
        //String str = "{{#ask: [[:Categoría:Seg Proyectos]] [[pepe]] [[luis|mas]] [[myProp::una]] }}";
        //TWikiItem doc = parser.parseText(str);

        System.out.println(doc);

        PrintWriter pw = new PrintWriter("C:\\WKSPs\\Consolidado\\WikiBKS\\src\\com\\jzb\\wiki\\output.txt");
        pw.println(doc);
        pw.close();

        for (TWikiItem item : doc.getByType(TWikiItem.TYPE.PROPERTY)) {
            System.out.println("----------------------------------------------");
            System.out.println(item);
        }
        
    }

}
