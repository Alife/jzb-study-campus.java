/**
 * 
 */
package com.isb.patch.aix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import com.isb.patch.UpdateException;

/**
 * UpdateStep to update Vega tool. It will update the properties file stored inside the plugin's folder with the new information.
 * 
 * @author PS00A501
 * 
 */
public class VegaUpdStep extends PluginUpdStepAbs {

    private static final String VEGA_BUNDLE_NAME = "vega_default.properties";
    private static final String VEGA_PLUGIN_NAME = "default.properties";
    private static final String VEGA_PLUGIN_ROOT = "com.isb.vega.model.repository_";

    /**
     * Default Constructor
     */
    public VegaUpdStep() {
        super();
    }

    /**
     * @see com.isb.patch.UpdatingStepAbs#getDescription()
     */
    public String getDescription() {
        return "Vega tool";
    }

    /**
     * @see com.isb.patch.aix.PluginUpdStepAbs#getBundleName()
     */
    protected String getBundleName() {
        return VEGA_BUNDLE_NAME;
    }

    /**
     * @see com.isb.patch.aix.PluginUpdStepAbs#getPluginNameRoot()
     */
    protected String getPluginNameRoot() {
        return VEGA_PLUGIN_ROOT;
    }

    /**
     * @see com.isb.patch.UpdatingStepAbs#getStepID()
     */
    protected String getStepID() {
        return "VegaUpdStep";
    }

    /**
     * This method creates a backup for the properties file and then uptades it. If something fails it tries to restore the backup.
     * 
     * @see com.isb.patch.aix.PluginUpdStepAbs#processPlugin(java.io.File, byte[])
     */
    protected void processPlugin(File pluginDir, Properties prop) throws UpdateException {

        String backName = VEGA_PLUGIN_NAME + "_" + new SimpleDateFormat("yyyy-M-d_HH_mm_ss").format(new Date());
        File propFile = new File(pluginDir, VEGA_PLUGIN_NAME);
        File backFile = new File(pluginDir, backName);

        // create a backup
        boolean backupMade = false;
        if (propFile.exists()) {
            if (!propFile.renameTo(backFile)) {
                getLogger().debug("ERROR: Creating properties backup");
                throw new UpdateException(ERROR_RENAMING_VEGA_PROPERTIES_1, "ERROR: Renaming properties file");
            }
            backupMade = true;
        }

        // Updates it
        try {
            // read current properties
            CommentedProperties currentProp = new CommentedProperties();
            
            FileInputStream fis=new FileInputStream(backFile);
            currentProp.load(fis);
            fis.close();

            // replace the values affected
            replacePropertiesValues(prop, currentProp);

            // write new properties
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(propFile.getAbsolutePath(), false);
                currentProp.store(fos, null);
            } finally {
                if (fos != null)
                    fos.close();
            }
        } catch (Throwable th) {
            // Tries to restore the backup
            if (backupMade) {
                propFile.delete();
                if (!backFile.renameTo(propFile)) {
                    getLogger().debug("ERROR: Writing new values. Backup couldn't be restored!!!", th);
                    throw new UpdateException(ERROR_RENAMING_VEGA_PROPERTIES_2, "ERROR: Renaming back properties file", th);
                }
            }
            getLogger().debug("Error writing new values. Backup was restored", th);
            throw new UpdateException(ERROR_WRITING_VEGA_PROPERTIES, "ERROR: Writing properties file in folder", th);
        }
    }
}
