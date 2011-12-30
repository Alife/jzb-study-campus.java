/**
 * 
 */
package com.jzb.mp3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class InfoLoader {

    private File                      m_pathFolder;
    private File                      m_infoFile;
    private TreeMap<String, FileInfo> m_infoList = new TreeMap<String, FileInfo>();

    public InfoLoader(String pathStr) {
        m_pathFolder = new File(pathStr);
        m_infoFile = new File(m_pathFolder, "mp3.info");
    }

    public Collection<FileInfo> getFileInfo() {
        return m_infoList.values();
    }

    public void loadInfo() throws Exception {

        Tracer._debug("Loading info from: " + m_pathFolder);
        if (!m_pathFolder.exists() || !m_pathFolder.isDirectory()) {
            Tracer._error("Info path doesn't exist or is not a directory: " + m_pathFolder);
            return;
        }

        m_infoList.clear();
        m_infoList = _parseInfoFile();
        HashMap<String, FileInfo> filesInfo = _readMP3Files(m_pathFolder);

        // Cuadra ambas listas, la de la informacion en fichero y la de los ficheros fisicos
        // Borra lo que no exista y añade lo nuevo como pending
        Iterator iter = m_infoList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, FileInfo> entry = (Map.Entry<String, FileInfo>) iter.next();
            Object o = filesInfo.remove(entry.getKey());
            if (o == null) {
                iter.remove();
            }
        }
        for (Map.Entry<String, FileInfo> entry : filesInfo.entrySet()) {
            m_infoList.put(entry.getKey(), entry.getValue());
        }

        saveInfo();
    }

    private TreeMap<String, FileInfo> _parseInfoFile() throws Exception {

        TreeMap<String, FileInfo> infoList = new TreeMap<String, FileInfo>();

        if (m_infoFile.exists()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(m_infoFile), "UTF-8"));
            while (br.ready()) {
                String cad = br.readLine();
                int p = cad.indexOf(',');
                FileInfo fi = new FileInfo();
                fi.state = FileInfoState.valueOf(cad.substring(0, p));
                fi.file = new File(cad.substring(p + 1).trim());
                fi.title = fi.file.getName();
                infoList.put(fi.file.getAbsolutePath(), fi);

            }
            br.close();
        }

        return infoList;
    }

    public void saveInfo() throws Exception {

        Tracer._debug("Saving info to: " + m_infoFile);

        File backFile = new File(m_infoFile.getAbsolutePath() + ".back");
        File f = new File(m_infoFile.getAbsolutePath());
        backFile.delete();
        f.renameTo(backFile);

        PrintStream ps = new PrintStream(m_infoFile, "UTF-8");
        if (m_infoFile != null) {
            for (FileInfo fi : m_infoList.values()) {
                ps.print(fi.state);
                ps.print(", ");
                ps.println(fi.file.getAbsolutePath());
            }
        }
        ps.close();

    }

    private HashMap<String, FileInfo> _readMP3Files(File folder) throws Exception {

        HashMap<String, FileInfo> infoList = new HashMap<String, FileInfo>();

        Tracer._debug("Reading info from folder: " + folder);
        for (File f : folder.listFiles()) {
            if (f.isDirectory()) {
                infoList.putAll(_readMP3Files(f));
            } else {
                if (f.getName().toLowerCase().endsWith(".mp3")) {
                    FileInfo fi = new FileInfo(f);
                    infoList.put(f.getAbsolutePath(), fi);
                }
            }
        }

        return infoList;
    }
}
