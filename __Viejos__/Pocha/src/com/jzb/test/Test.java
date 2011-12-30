/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.test;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**
 * @author PS00A501
 */
public class Test {
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
            Test me = new Test();
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
        Properties p1= new Properties();
        p1.load(Test.class.getClassLoader().getResourceAsStream("functions_1.properties"));
        Properties p2= new Properties();
        p2.load(Test.class.getClassLoader().getResourceAsStream("functions_2.properties"));
        
        Iterator it1=p2.keySet().iterator();
        while(it1.hasNext()) {
            Object k1=it1.next();
            if(!p1.keySet().contains(k1)) {
                System.out.println(k1);
            }
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
    public void doIt1(String[] args) throws Exception {
        String input="D:\\JZarzuela\\DOCs\\Privados\\Pocha\\pocha_test.xls";
        File inputWorkbook = new File(input);

//        File outputWorkbook = new File(input);
//        WritableWorkbook w2 = Workbook.createWorkbook(outputWorkbook);
//        WritableSheet ws=w2.getSheet("Hoja1");
//        Cell c2=ws.findLabelCell("Native");
//        int cr2=c2.getRow();
//        int cc2=c2.getColumn();
//        WritableCell wc=ws.getWritableCell(cc2, cr2+1);
//        w2.close();
        
        Workbook w1 = Workbook.getWorkbook(inputWorkbook);
        Sheet s=w1.getSheet("Pocha-1");
        Cell c=s.getCell('N'-'A',0);
        c.toString();
        System.out.println(s.getSettings());
        
        w1.close();
    }
}
