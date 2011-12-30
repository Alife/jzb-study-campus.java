/**
 * 
 */
package com.isb.patch;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Main class. Its behaviour is:
 * <ol>
 * <li>If defined, it shows a "warning text" to the user to inform them about the objectives. Additionally, it gives them the chance of canceling the execution. <br>
 * The information can be just plain text or HTML to create rich texts.
 * <li> Loads updating steps' class names from the "cfg/steps.properties" file to be executed.
 * <li> Execute all the steps using a graphical interface.
 * </ol>
 * <br>
 * This class redirects all the Logging information to a file residing in the use home_path. <br>
 * <br>
 * Finally, no error messages are show to the user, just <b>Error Codes</b>. It is because it could be sensible info. <br>
 * <br>
 * 
 * @author IS201105
 * 
 */
public class UpdaterMain {

    private static final String STEPS_PROPERTIES_FILE = "/cfg/steps.properties";
    private static final String WARNING_TEXT_FILE     = "/cfg/warning.txt";

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {

            String msg = readWarningMessage();
            IUpdatingStep steps[] = readSteps();

            if (msg != null) {
                if (!JDWarning.showIt(msg)) {
                    System.exit(0);
                }
            }

            String homePath = System.getProperty("user.home");
            if (homePath == null)
                homePath = ".";
            String logFileName = homePath + File.separator + "patcher_" + System.currentTimeMillis() + "_.log";
            FileLogger logger = new FileLogger(logFileName);
            JDUpdater.showIt(logger, steps);
            logger.close();

        } catch (Throwable th) {
            String message = "Codigo del error:\nError code:\n\n0xF000FFFF\n\n";
            JOptionPane.showMessageDialog(new JFrame(), message, "Error ejecutanto / Executing", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }

    /**
     * This method reads the Updating Steps to be executed. Their class names are read from "cfg/steps.properties" resource file.
     * 
     * @return An array containing the Updating Steps instances to be executed.
     * 
     * @throws Exception
     *             If there is an error.
     */
    private static IUpdatingStep[] readSteps() throws Exception {

        ArrayList list = new ArrayList();
        Properties prop = new Properties();
        prop.load(UpdaterMain.class.getResourceAsStream(STEPS_PROPERTIES_FILE));
        Enumeration e = prop.elements();
        while (e.hasMoreElements()) {
            String className = (String) e.nextElement();
            Class clazz = Class.forName(className);
            IUpdatingStep obj = (IUpdatingStep) clazz.newInstance();
            list.add(obj);
        }

        IUpdatingStep result[] = new IUpdatingStep[list.size()];
        for (int n = 0; n < result.length; n++) {
            result[n] = (IUpdatingStep) list.get(n);
        }

        return result;
    }

    /**
     * This method will read the text/HTML message contained in the resource file "cfg/warning.text".
     * 
     * @return The message read or <i>null</i> if it doesn't exist.
     * 
     * @throws Exception
     *             If there is an error.
     */
    private static String readWarningMessage() throws Exception {
        InputStream is = UpdaterMain.class.getResourceAsStream(WARNING_TEXT_FILE);
        if (is != null) {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while (br.ready()) {
                sb.append(br.readLine());
            }
            return sb.toString();
        }
        return null;
    }

}
