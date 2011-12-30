/**
 * 
 */

/**
 * @author n000013
 * 
 */
public class SNGenerator {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            SNGenerator me = new SNGenerator();
            me.doIt(args);
        } catch (Throwable th) {
            System.out.println("***** FAILED *****");
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

        System.out.println("***********************************************************************");
        System.out.println("Designer v5.1.0 for Eclipse - Serial Number & Activation Code Generator");
        System.out.println("***********************************************************************");
        System.out.println();
        
        String MAC_ADDRS = readMacAddr();

        String strLeft = MAC_ADDRS + "SWTDESIGNERPRO" + "1JZBX1X";

        long leftNumber = _S(strLeft);

        String s = calcRightStr(leftNumber);
        if (s.length() != 7) {
            s = calcRightStr(leftNumber + 0x100000000L);
        }
        if (s.length() != 7) {
            System.out.println("Error: Can't calculate a 7 digits string for the input data!");
        }

        String ActivationStr = "SWTDesignerPro-1JZBX-1X" + s.substring(0, 3) + "-" + s.substring(3, 5) + "X" + s.substring(5);
        System.out.println();
        System.out.println("Serial Number:   SWTDesignerPro-483428891");
        System.out.println("Activation Code: "+ActivationStr);
        System.out.println();
    }

    private String readMacAddr() throws Exception {

        StringBuffer sb = new StringBuffer();

        System.out.print("Enter MAC Address (ej: 00-1B-38-B8-C6-8C): ");
        sb.append((char) System.in.read());
        while (System.in.available() > 0) {
            char c = (char) System.in.read();
            if (c > ' ')
                sb.append(c);
        }
        return sb.toString();
    }

    private String calcRightStr(long leftNumber) {
        final String digits = "0123456789ABCDEFGHJKMNPQRSTUVWXYZ";
        long decNumber = leftNumber;
        String s = "";
        while (decNumber > 0) {
            int i = (int) (decNumber % 33L);
            s = digits.charAt(i) + s;
            decNumber = decNumber / 33;
        }
        return s;
    }

    private int _S(java.lang.String s)
    {
        int i = 0;
        for (int j = 0; j < s.length(); j++)
        {
            char c = s.charAt(j);
            if (c < '\177')
            {
                if (i > 0x3fffffff)
                {
                    i = (i - 0x40000000) * 2 + 1;
                } else
                {
                    i *= 2;
                }
                if (i > 0x7fffffff - c)
                {
                    i -= 0x40000000;
                }
                i += c;
            }
        }

        return i;
    }
    
}
