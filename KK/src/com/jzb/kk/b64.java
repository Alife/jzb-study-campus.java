/**
 * 
 */
package com.jzb.kk;

import java.io.FileOutputStream;

import com.jzb.ipa.plist.BinaryPListParser;
import com.jzb.util.Base64;

/**
 * @author n63636
 * 
 */
public class b64 {

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
            b64 me = new b64();
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
        String s =
                
                
                "YnBsaXN0MDDUAQIDBAUIQ0RUJHRvcFgkb2JqZWN0c1gkdmVyc2lvblkkYXJjaGl2ZXLR"+
                "BgdUcm9vdIABrxARCQoZGhscHSMkJS0zNDo7PEJVJG51bGzTCwwNDhMYWk5TLm9iamVj"+
                "dHNXTlMua2V5c1YkY2xhc3OkDxAREoAGgAqADIAPpBQVFheAAoADgASABYAJXxAjY29t"+
                "Lm1pbmljbGlwLkZyYWdnZXIuc29sdXRpb24uMTEuMTVfECJjb20ubWluaWNsaXAuRnJh"+
                "Z2dlci5zb2x1dGlvbi4xMS41XxAbY29tLm1pbmljbGlwLkZyYWdnZXIuc2tpcHM1XxAf"+
                "Y29tLm1pbmljbGlwLkZyYWdnZXIuc29sdXRpb25zNdMLDA0eIBihH4AIoSGAB4AJVWNv"+
                "dW50EAHSJicoLFgkY2xhc3Nlc1okY2xhc3NuYW1loykqK18QE05TTXV0YWJsZURpY3Rp"+
                "b25hcnlcTlNEaWN0aW9uYXJ5WE5TT2JqZWN0XxATTlNNdXRhYmxlRGljdGlvbmFyedML"+
                "DA0uMBihH4AIoTGAC4AJVWNvdW500wsMDTU3GKE2gA6hOIANgAlVY291bnQQA9MLDA09"+
                "PxihPoAQoSGAB4AJEAASAAGGoF8QD05TS2V5ZWRBcmNoaXZlcgAIABEAFgAfACgAMgA1"+
                "ADoAPABQAFYAXQBoAHAAdwB8AH4AgACCAIQAiQCLAI0AjwCRAJMAuQDeAPwBHgElAScB"+
                "KQErAS0BLwE1ATcBPAFFAVABVAFqAXcBgAGWAZ0BnwGhAaMBpQGnAa0BtAG2AbgBugG8"+
                "Ab4BxAHGAc0BzwHRAdMB1QHXAdkB3gAAAAAAAAIBAAAAAAAAAEUAAAAAAAAAAAAAAAAA"+
                "AAHw";
                
        byte b[] = Base64.decode(s);

        BinaryPListParser bplp = new BinaryPListParser();
        bplp.parsePList(b);

        System.out.println(new String(b));
        FileOutputStream fos = new FileOutputStream("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\kk\\Fragger.plist");
        fos.write(b);
        fos.close();
        System.out.println(s);
    }
}
