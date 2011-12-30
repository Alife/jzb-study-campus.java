/**
 * 
 */
package com.isb.patch.aix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.isb.patch.UpdateException;

/**
 * UpdateStep 1 to update Deneb tool. It will update the properties file stored inside the plugin's JAR file with the new information.
 * 
 * WARNING: Plugin versions below 1.8.8 has usr/pwd in clear text. Versions above that are ciphered.
 * 
 * @author PS00A501
 * 
 */
public class DenebUpdStep2 extends PluginUpdStepAbs {

    private static final String DENEB_BUNDLE_NAME   = "deneb2_default.properties";
    private static final String DENEB_JAR_FILE_NAME = "DenebVegaPluginResources.properties";
    private static final String DENEB_PLUGIN_NAME   = "DenebVega.jar";
    private static final String DENEB_PLUGIN_ROOT   = "com.isb.deneb.provider.vega_";

    /**
     * Default Constructor
     */
    public DenebUpdStep2() {
        super();
    }

    /**
     * @see com.isb.patch.UpdatingStepAbs#getDescription()
     */
    public String getDescription() {
        return "Deneb tool upd-2";
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
        return "DenebUpdStep2";
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
            boolean ciphered = areCiphered(pluginDir.getName());
            replaceFileInJAR(ciphered, backFile, jarFile, prop);
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
     * @param areCiphered
     *            Indicate if ciphered or clear text values have to be used
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
    private void replaceFileInJAR(boolean areCiphered, File origJAR, File newJAR, Properties newData) throws Exception {

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
            replacePropertiesValues(areCiphered, newData, currentProp);

            // adds new properties file
            zout.putNextEntry(new ZipEntry(DENEB_JAR_FILE_NAME));
            currentProp.store(zout, null);
            zout.closeEntry();
        } finally {
            if (zin != null)
                zin.close();
            if (zout != null)
                zout.close();
        }
    }

    /**
     * This method iterates the new properties to be used and set their values in the previous properties file
     * 
     * @param areCiphered
     *            Indicate if ciphered or clear text values have to be used
     * 
     * @param newProp
     *            new values to be used
     * 
     * @param currentProp
     *            current values to be updated
     * 
     * @throws UpdateException
     *             if something fails
     */
    protected void replacePropertiesValues(boolean areCiphered, Properties newProp, CommentedProperties currentProp) throws UpdateException {

        Iterator iter = newProp.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            if (!areCiphered && key.startsWith("v1_"))
                currentProp.setProperty(key.substring(3), val);
            if (areCiphered && key.startsWith("v2_"))
                currentProp.setProperty(key.substring(3), val);
        }
    }

    /**
     * Calculates if plugin version is below or above "1.8.8". This is because versions equal or below that has usr/pwd in clear text. Above that are ciphered.
     * 
     * WARNING: This algorithm assumes that version is in the form of "x.y.z" And that it is between "0" and "000".
     * 
     * @param pluginDirName
     *            Plugin's name containing the version
     * @return TRUE if plugin's version is above "1.8.8" indicating that ciphered data has to be used.
     * @throws Exception
     *             if something fails.
     */
    private boolean areCiphered(String pluginDirName) throws Exception {

        int pos1 = 1 + pluginDirName.lastIndexOf('_');
        int pos2 = 1 + pluginDirName.indexOf('.', pos1);
        int pos3 = 1 + pluginDirName.indexOf('.', pos2);

        int V1 = Integer.parseInt(pluginDirName.substring(pos1, pos2 - 1));
        int V2 = Integer.parseInt(pluginDirName.substring(pos2, pos3 - 1));
        int V3 = Integer.parseInt(pluginDirName.substring(pos3));

        int tV = 1000000 * V1 + 1000 * V2 + V3;

        return tV > 1008008;
    }
}
