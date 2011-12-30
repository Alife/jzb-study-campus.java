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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.isb.patch.UpdateException;

/**
 * UpdateStep 1 to update Deneb tool. It will update the properties file stored inside the plugin's JAR file with the new information.
 * 
 * @author PS00A501
 * 
 */
public class DenebUpdStep1 extends PluginUpdStepAbs {

    private static final String DENEB_BUNDLE_NAME   = "deneb1_default.properties";
    private static final String DENEB_JAR_FILE_NAME = "com/isb/deneb/internal/model/dbapi/DBApiPluginResources.properties";
    private static final String DENEB_PLUGIN_NAME   = "dbapi.jar";
    private static final String DENEB_PLUGIN_ROOT   = "com.isb.deneb.model.dbapi_";

    /**
     * Default Constructor
     */
    public DenebUpdStep1() {
        super();
    }

    /**
     * @see com.isb.patch.UpdatingStepAbs#getDescription()
     */
    public String getDescription() {
        return "Deneb tool upd-1";
    }

    /**
     * @see com.isb.patch.aix.PluginUpdStepAbs#getBundleName()
     */
    protected String getBundleName() {
        return DENEB_BUNDLE_NAME;
    }

    /**
     * @see com.isb.patch.aix.PluginUpdStepAbs#getPluginNameRoot()
     */
    protected String getPluginNameRoot() {
        return DENEB_PLUGIN_ROOT;
    }

    /**
     * @see com.isb.patch.UpdatingStepAbs#getStepID()
     */
    protected String getStepID() {
        return "DenebUpdStep1";
    }

    /**
     * This method creates a backup for the JAR file and then uptades it. If something fails it tries to restore the backup.
     * 
     * @see com.isb.patch.aix.PluginUpdStepAbs#processPlugin(java.io.File, byte[])
     */
    protected void processPlugin(File pluginDir, Properties prop) throws UpdateException {

        String backName = DENEB_PLUGIN_NAME + "_" + new SimpleDateFormat("yyyy-M-d_HH_mm_ss").format(new Date());
        File jarFile = new File(pluginDir, DENEB_PLUGIN_NAME);
        File backFile = new File(pluginDir, backName);

        // create a backup
        boolean backupMade = false;
        if (jarFile.exists()) {
            if (!jarFile.renameTo(backFile)) {
                getLogger().debug("ERROR: Creating properties backup");
                throw new UpdateException(ERROR_RENAMING_DENEB_PROPERTIES_1, "ERROR: Renaming properties file");
            }
            backupMade = true;
        }

        // Updates it
        try {
            replaceFileInJAR(backFile, jarFile, prop);
        } catch (Throwable th) {
            // Tries to restore the backup
            if (backupMade) {
                jarFile.delete();
                if (!backFile.renameTo(jarFile)) {
                    getLogger().debug("ERROR: Writing new values. Backup couldn't be restored!!!", th);
                    throw new UpdateException(ERROR_RENAMING_DENEB_PROPERTIES_2, "ERROR: Renaming back properties file", th);
                }
            }
            getLogger().debug("Error writing new values. Backup was restored", th);
            throw new UpdateException(ERROR_WRITING_DENEB_PROPERTIES, "ERROR: Writing properties file in folder", th);
        }
    }

    /**
     * Utility method that allow to update the properties file's info inside the JAR file. To do it, it copies the backup JAR file info to other one skipping the file to be updated.
     * 
     * @param origJAR
     *            Backed up JAR file with the original information
     * 
     * @param newJAR
     *            New plugin JAR file to be created with the updated info
     * 
     * @param newData
     *            Updated info to be used
     * 
     * @throws Exception
     *             If something fails
     */
    private void replaceFileInJAR(File origJAR, File newJAR, Properties newData) throws Exception {

        ZipInputStream zin = null;
        ZipOutputStream zout = null;
        try {
            CommentedProperties currentProp = new CommentedProperties();

            zin = new ZipInputStream(new FileInputStream(origJAR));
            zout = new ZipOutputStream(new FileOutputStream(newJAR));

            // Copy the original JAR skipping the properties file
            byte buf[] = new byte[1024];
            ZipEntry entry = zin.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                boolean isPropFile = name.equals(DENEB_JAR_FILE_NAME);
                if (isPropFile) {
                    currentProp.load(zin);
                } else {
                    zout.putNextEntry(new ZipEntry(name));
                    int len;
                    while ((len = zin.read(buf)) > 0) {
                        zout.write(buf, 0, len);
                    }
                }
                entry = zin.getNextEntry();
            }

            // replace the values affected
            replacePropertiesValues(newData, currentProp);

            // adds new properties file
            zout.putNextEntry(new ZipEntry(DENEB_JAR_FILE_NAME));
            currentProp.store(zout, null);
            zout.closeEntry();
        } finally {
            if(zin!=null) zin.close();
            if(zout!=null) zout.close();
        }
    }
}
