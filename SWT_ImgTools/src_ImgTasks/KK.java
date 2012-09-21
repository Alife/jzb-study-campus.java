import java.io.File;

/**
 * 
 */

/**
 * @author jzarzuela
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

        File baseFolder = new File("/Users/jzarzuela/Documents/_TMP_/100CANON-PRAGA/Filtradas_NO");
        _cleanNames(baseFolder);
    }

    // --------------------------------------------------------------------------------------------------------
    private void _cleanNames2(File folder) throws Exception {
        
        System.out.println("Cleaning names in folder: " + folder);
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _cleanNames(f);
                if(f.getName().startsWith("@")) {
                    String newName = f.getName().substring(1);
                    File newFile = new File(f.getParentFile(), newName);
                    if (!f.renameTo(newFile)) {
                        System.out.println("  Error cleaning file: " + f);
                    }
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------
    private void _cleanNames(File folder) throws Exception {

        System.out.println("Cleaning names in folder: " + folder);
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                _cleanNames(f);
            } else {
                String newName = f.getName();
                if (newName.toLowerCase().endsWith(".jpg")) {
                    newName = newName.replaceAll("0\\*", "");
                    newName = newName.replaceAll("1\\*", "");
                    newName = newName.replaceAll("2\\*", "");
                    newName = newName.replaceAll("3\\*", "");
                    newName = newName.replaceAll("4\\*", "");
                    newName = newName.replaceAll("5\\*", "");
                    newName = newName.replaceAll("6\\*", "");
                    newName = newName.replaceAll("7\\*", "");
                    newName = newName.replaceAll("8\\*", "");
                    newName = newName.replaceAll("9\\*", "");

                    newName = newName.replaceAll("0\\%", "");
                    newName = newName.replaceAll("1\\%", "");
                    newName = newName.replaceAll("2\\%", "");
                    newName = newName.replaceAll("3\\%", "");
                    newName = newName.replaceAll("4\\%", "");
                    newName = newName.replaceAll("5\\%", "");
                    newName = newName.replaceAll("6\\%", "");
                    newName = newName.replaceAll("7\\%", "");
                    newName = newName.replaceAll("8\\%", "");
                    newName = newName.replaceAll("9\\%", "");

                    newName = newName.replaceAll("-SIN NADA", "");

                    File newFile = new File(f.getParentFile(), newName);
                    if (!f.renameTo(newFile)) {
                        System.out.println("  Error cleaning file: " + f);
                    }
                }
            }
        }

    }

}
