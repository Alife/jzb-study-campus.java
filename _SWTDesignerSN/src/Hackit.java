import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.instantiations.common.ui.wizard.ActivationWizard;
import com.instantiations.common.ui.wizard.ActivationWizardDialog;
import com.instantiations.common.ui.wizard.ActivationWizardFirstPage;

/**
 * 
 */

/**
 * @author n000013
 * 
 */
public class Hackit {

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
            Hackit me = new Hackit();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
        }
    }

    public void doIt2(String[] args) throws Exception {


        String MAC_ADDRS = "00-1B-38-B8-C6-8C";

        String strLeft = MAC_ADDRS + "SWTDESIGNERPRO" + "1JZBX1X";

        long leftNumber = com.instantiations.common.A.A.A.S(strLeft);

        String s = calcRightStr(leftNumber);
        if (s.length() != 7) {
            s = calcRightStr(leftNumber + 0x100000000L);
        }
        if (s.length() != 7) {
            System.out.println("Error: Can't calculate a 7 digits string for the input data!");
        }
        
        String ActivationStr = "SWTDesignerPro-1JZBX-1X"+s.substring(0,3)+"-"+s.substring(3,5)+"X"+s.substring(5);
        System.out.println(s);
        System.out.println(ActivationStr);
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
        return  s;
    }

    public void doIt(String[] args) throws Exception {

        // ActivationWizardFirstPage

        final Display display = Display.getDefault();
        Shell shell = new Shell(display);

        ActivationWizard aw = new ActivationWizard();
        ActivationWizardDialog awd = new ActivationWizardDialog(shell, aw);
        awd.open();

    }
}
