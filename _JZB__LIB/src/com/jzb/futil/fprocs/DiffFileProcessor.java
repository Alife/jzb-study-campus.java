/**
 * 
 */
package com.jzb.futil.fprocs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

import com.jzb.futil.FolderIterator;
import com.jzb.futil.IFileProcessor;

/**
 * @author n63636
 * 
 */
public class DiffFileProcessor implements IFileProcessor {

    private HashMap<String, ArrayList<File>> m_processedFiles = new HashMap<String, ArrayList<File>>();

    public DiffFileProcessor() {

    }

    public ArrayList<ArrayList<File>> getDuplicatedFiles() {
        ArrayList<ArrayList<File>> dupFiles= new ArrayList<ArrayList<File>>();
        for(ArrayList<File> af:m_processedFiles.values()) {
            if(af.size()>1) {
                dupFiles.add(af);
            }
        }
        return dupFiles;
    }
    
    public Collection<File> getSingleFiles() {
        TreeMap<String,File> singleFiles= new TreeMap<String,File>();
        for(ArrayList<File> af:m_processedFiles.values()) {
            if(af.size()==1) {
                File f=af.get(0);
                singleFiles.put(f.getAbsolutePath(),f);
            }
        }
        return singleFiles.values();
    }

    /**
     * @see com.jzb.futil.IFileProcessor#processFile(java.io.File, java.io.File)
     */
    @Override
    public void processFile(File f, File baseFolder) throws Exception {

        String key = _calcKey(f);
        ArrayList<File> array = _getArrayByKey(key);
        array.add(f);

    }

    /**
     * @see com.jzb.futil.IFileProcessor#setFolderIterator(com.jzb.futil.FolderIterator)
     */
    @Override
    public void setFolderIterator(FolderIterator fi) {

    }

    private String _calcKey(File f) throws Exception {
        return f.getName() + "#" + f.length();
    }

    private ArrayList<File> _getArrayByKey(String key) {

        ArrayList<File> array = m_processedFiles.get(key);
        if (array == null) {
            array = new ArrayList<File>();
            m_processedFiles.put(key, array);
        }
        return array;
    }

}
