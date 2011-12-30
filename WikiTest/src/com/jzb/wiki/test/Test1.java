/**
 * 
 */
package com.jzb.wiki.test;

import com.jzb.util.Des3Encrypter;
import com.jzb.wiki.util.BKSWikiHelper;


/**
 * @author n000013
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
       BKSWikiHelper helper = new BKSWikiHelper();
       helper.login(Des3Encrypter.decryptStr("PjN1Jb0t6CY0Eo9zcFVohw=="),Des3Encrypter.decryptStr("PjN1Jb0t6CYD25gJXVCyxw=="));
       //helper.navigateTo("?title=VV_BKS&action=edit","Editando VV BKS - BanksphereWiki");
       helper.navigateTo("?title=JZB_TEST&action=edit","Editandox JZB TEST - BanksphereWiki");
       
       //helper.changeEditingText("Una prueba Mas", "Porque quiero", false, "JZB TEST - BanksphereWiki");
       System.out.println(helper.getTitle());
       
    }

}
