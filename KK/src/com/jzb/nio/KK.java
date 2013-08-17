/**
 * 
 */
package com.jzb.nio;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @author jzarzuela
 * 
 */
public class KK {

    // ----------------------------------------------------------------------------------------------------
    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("\n***** EXECUTION STARTED *****\n");
            KK me = new KK();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("\n***** EXECUTION FINISHED [" + (t2 - t1) + "]*****\n");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("\n***** EXECUTION FAILED *****\n");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        File wkspFolder = new File("/Users/jzarzuela/Documents/java-Campus");
        _processWKSPFolder(wkspFolder);
    }

    private void _processWKSPFolder(File folder) throws Exception {

        System.out.println("Processing WKSP folder: " + folder);
        File files[] = folder.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File f : files) {
            if (f.isDirectory()) {

                if (f.getName().startsWith("."))
                    continue;

                File settingsFolder = new File(f, ".settings");
                if (!settingsFolder.exists()) {
                    settingsFolder.mkdirs();
                }

                _processSettingsFolder(settingsFolder);
            }
        }

    }

    private static final String defaultData = "eclipse.preferences.version=1\nencoding/<project>=ISO-8859-1\n";

    private void _processSettingsFolder(File folder) throws Exception {

        //System.out.println("Processing Settings folder: " + folder);
        File prefs = new File(folder, "org.eclipse.core.resources.prefs");
        if (prefs.exists()) {
            System.out.println("Existen prefs: " + folder);
            _processResourcesFile(prefs);
        } else {
            System.out.println("+ Creando encoding en: " + prefs);
            _saveResourcesFile(prefs, defaultData);
        }
    }

    private void _processResourcesFile(File f) throws Exception {

        char buffer[] = new char[(int) f.length()];
        FileReader fr = new FileReader(f);
        int len = fr.read(buffer, 0, buffer.length);
        fr.close();

        String data = new String(buffer, 0, len);
        if (!data.contains("encoding/<project>=ISO-8859-1")) {
            System.out.println(" Añadiendo encoding a: " + f);
            String newData = data + "\nencoding/<project>=ISO-8859-1\n";
            _saveResourcesFile(f, newData);
        }
    }

    private void _saveResourcesFile(File f, String newData) throws Exception {
        FileWriter fw = new FileWriter(f);
        fw.write(newData);
        fw.close();
    }

}
