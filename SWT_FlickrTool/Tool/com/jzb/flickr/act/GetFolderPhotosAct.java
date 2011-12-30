/**
 * 
 */
package com.jzb.flickr.act;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class GetFolderPhotosAct extends BaseAction {

    private Pattern m_excludedFoldersRegExp;
    private File    m_folder;
    private boolean m_recursive;
    private Pattern m_regExp;

    public GetFolderPhotosAct(ITracer tracer) {
        super(tracer);
        m_regExp = Pattern.compile(".*\\.jpg", Pattern.CASE_INSENSITIVE);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {
        return _execute(m_folder);
    }

    /**
     * @return the regExp
     */
    public String getExcludedFoldersRegExp() {
        return m_excludedFoldersRegExp.pattern();
    }

    /**
     * @return the folderName
     */
    public String getFolderName() {
        return m_folder.getAbsolutePath();
    }

    /**
     * @return the recursive
     */
    public String getRecursive() {
        return m_recursive ? "true" : "false";
    }

    /**
     * @return the regExp
     */
    public String getRegExp() {
        return m_regExp.pattern();
    }

    /**
     * @param regExp
     *            the pattern to set
     */
    public void setExcludedFoldersRegExp(String regExp) {
        m_excludedFoldersRegExp = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
    }

    /**
     * @param folderName
     *            the folderName to set
     */
    public void setFolderName(String folderName) {
        m_folder = new File(folderName);
    }

    /**
     * @param recursive
     *            the recursive to set
     */
    public void setRecursive(String recursive) {
        m_recursive = Boolean.parseBoolean(recursive);
    }

    /**
     * @param regExp
     *            the pattern to set
     */
    public void setRegExp(String regExp) {
        m_regExp = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
    }

    private ArrayList<File> _execute(File baseFolder) throws Exception {

        ArrayList<File> files = new ArrayList<File>();

        m_tracer._debug("Getting photos from folder: " + baseFolder);

        if (!baseFolder.exists() || !baseFolder.isDirectory())
            return files;

        for (File file : baseFolder.listFiles()) {

            if (file.isDirectory()) {
                if (m_recursive) {
                    files.addAll(_execute(file));
                }
            } else {
                
                boolean match = m_regExp.matcher(file.getName()).matches();
                boolean excluded = m_excludedFoldersRegExp == null ? false : m_excludedFoldersRegExp.matcher(file.getParent()).matches();
                
                if (match && !excluded) {
                    files.add(file);
                } else {
                    m_tracer._debug("  File filtered: " + file.getName());
                }
            }
        }

        return files;
    }
}
