/**
 * 
 */
package com.isb.patch.aix;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.isb.patch.UpdateException;
import com.isb.patch.UpdatingStepAbs;

/**
 * This abstract class provides the common functionality that these steps share:
 * <ul>
 * <li>Find WSAD base path
 * <li>Find all versions for the plugin being searched
 * <li>Read the new information that will update the current one
 * <li><b>Delegate</b> to its subclass how the information is actually used to update the plugin
 * </ul>
 * 
 * @author PS00A501
 * 
 */
public abstract class PluginUpdStepAbs extends UpdatingStepAbs implements IErrors {

    private static final String WSAD_REL_PATH    = ":\\Archivos de programa\\IBM\\WebSphere Studio\\eclipse\\plugins";

    // To avoid searching it twice
    private static File         s_lastWSADFolder = null;

    /**
     * Default Constructor
     */
    public PluginUpdStepAbs() {
        super();
    }

    /**
     * @see com.isb.patch.UpdatingStepAbs#getDescription()
     */
    public abstract String getDescription();

    /**
     * This method search inside the WSAD base path for plugins which match the searching criteria. Now, the searching criteria is just to star with a certain name root.
     * 
     * @param wsadBasePath
     *            The WSAD path to be used in the searching
     * 
     * @return The list of Plugin Files which match the searching criteria.
     * 
     * @throws UpdateException
     *             If something fails
     */
    protected ArrayList findPluginFolders(File wsadBasePath) throws UpdateException {

        ArrayList folders = new ArrayList();
        File files[] = wsadBasePath.listFiles();
        for (int n = 0; n < files.length; n++) {
            if (files[n].isDirectory() && files[n].getName().startsWith(getPluginNameRoot())) {
                folders.add(files[n]);
            }
        }
        return folders;
    }

    /**
     * @return Will return the folder where WSAD is installed.
     * 
     * @throws UpdateException
     *             If WSAD folder cannot be found
     */
    protected File findWSADPath() throws UpdateException {

        File wsadBasePath;

        if (s_lastWSADFolder != null) {
            return s_lastWSADFolder;
        }

        wsadBasePath = new File("C" + WSAD_REL_PATH);
        if (wsadBasePath.exists()) {
            s_lastWSADFolder = wsadBasePath;
            return wsadBasePath;
        }

        wsadBasePath = new File("D" + WSAD_REL_PATH);
        if (wsadBasePath.exists()) {
            s_lastWSADFolder = wsadBasePath;
            return wsadBasePath;
        }

        String message = "Error al intentar localizar la carpeta de plugins de WSAD.\nPor favor, indíquelo manualmente.\nSe busco en:\n    " + wsadBasePath;
        message += "\n\n----------------------------------------------------------------------------------------------------------\n\n";
        message += "Error trying to locate  WSAD's plugins folder.\nPlease, Select it manually.\nIt was searched in:\n    " + wsadBasePath;

        JOptionPane.showMessageDialog(new JFrame(), message, "Error ejecutando / Executing", JOptionPane.ERROR_MESSAGE);
        JFileChooser fc = new JFileChooser(wsadBasePath);
        fc.setDialogTitle("Seleccione/Select 'plugins'");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            wsadBasePath = fc.getSelectedFile();
            if (wsadBasePath.getAbsolutePath().endsWith("eclipse" + File.separator + "plugins")) {
                s_lastWSADFolder = wsadBasePath;
                return wsadBasePath;
            }
        }

        getLogger().debug("ERROR: WSAD base path not found!!!");
        throw new UpdateException(ERROR_WSAD_FOLDER_NOT_FOUND, "WSAD folder not found");
    }

    /**
     * @return Resource bundle's name to be read that contains the new data to be used as update
     */
    protected abstract String getBundleName();

    /**
     * @return Plugin name's root to be used as searching criteria
     */
    protected abstract String getPluginNameRoot();

    /**
     * @see com.isb.patch.UpdatingStepAbs#getStepID()
     */
    protected abstract String getStepID();

    /**
     * Here, all the common tasks are accomplished. Finally, the actual subclass is called to finish the updating process.
     * 
     * @see com.isb.patch.UpdatingStepAbs#innerexecuteStep()
     */
    protected void innerexecuteStep() throws UpdateException {

        if (wasCanceled())
            return;

        getLogger().debug("Searching WSAD base path...");
        File wsadBasePath = findWSADPath();
        getMonitor().updateProgress(10);

        if (wasCanceled())
            return;

        getLogger().debug("Reading boundle data...");
        Properties prop = readBoundleData();
        getMonitor().updateProgress(20);

        if (wasCanceled())
            return;

        getLogger().debug("Searching for plugin's folders...");
        ArrayList folders = findPluginFolders(wsadBasePath);
        getMonitor().updateProgress(30);

        int val = 30;
        int incr = folders.size() > 0 ? (70 / folders.size()) : 0;

        for (int n = 0; !wasCanceled() && n < folders.size(); n++) {

            File pluginFolder = (File) folders.get(n);

            // In order to not show the actual plugin name, just the version is used
            String hint = pluginFolder.getName().substring(getPluginNameRoot().length());
            getLogger().debug("Processing plugin: '" + hint + "'");

            // HERE THE REAL WORK IS DONE
            processPlugin(pluginFolder, prop);

            val += incr;
            getMonitor().updateProgress(val);

        }
    }

    /**
     * Method to be implemented by subclasses where the real updating work is done
     * 
     * @param pluginFolder
     *            The folder that contains the plugin info to be updated
     * 
     * @param prop
     *            Information to be used to update the plugin
     * 
     * @throws UpdateException
     *             If something fails
     */
    protected abstract void processPlugin(File pluginFolder, Properties prop) throws UpdateException;

    /**
     * This method reads the data to be used in the updating process from a resource bundle
     * 
     * @return Properties containing the new info
     * 
     * @throws UpdateException
     *             If something fails
     */
    protected Properties readBoundleData() throws UpdateException {

        try {
            Properties prop = new Properties();
            prop.load(getClass().getResourceAsStream(getBundleName()));
            return prop;
        } catch (Throwable th) {
            getLogger().debug("Error reading properties boundle", th);
            throw new UpdateException(ERROR_READING_PROPERTIES_BUNDLE, "ERROR: Reading bundle with new properties", th);
        }
    }
    
    /**
     * This method iterates the new properties to be used and set their values in
     * the previous properties file
     *  
     * @param newProp new values to be used
     * @param currentProp current values to be updated
     * @throws UpdateException if something fails
     */
    protected void replacePropertiesValues(Properties newProp, CommentedProperties currentProp) throws UpdateException {

        Iterator iter = newProp.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            currentProp.setProperty(key, val);
        }
    }
}
