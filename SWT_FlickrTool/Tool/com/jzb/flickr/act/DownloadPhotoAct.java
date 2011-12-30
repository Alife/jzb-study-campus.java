/**
 * 
 */
package com.jzb.flickr.act;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.aetrion.flickr.photos.Photo;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class DownloadPhotoAct extends BaseAction {

    private String m_albumName;

    private String m_baseFolder;

    private Photo  m_photo;

    public DownloadPhotoAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        m_tracer._debug("Downloading photo: " + m_photo.getTitle());

        if (_getFinalFile().exists()) {
            m_tracer._warn("Downloading skipped. Photo <" + m_photo.getTitle() + "> already exists in destination folder");
            return null;
        }

        URL url = new URL(m_photo.getOriginalUrl());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        BufferedInputStream inStream = new BufferedInputStream(conn.getInputStream());

        File outFile = _getTmpFile();
        outFile.getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(outFile);

        byte buffer[] = new byte[204800];
        int read;
        while ((read = inStream.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.flush();
        fos.close();
        inStream.close();

        _getTmpFile().renameTo(_getFinalFile());

        return null;
    }

    /**
     * @return the albumName
     */
    public String getAlbumName() {
        return m_albumName;
    }

    /**
     * @return the baseFolder
     */
    public String getBaseFolder() {
        return m_baseFolder;
    }

    /**
     * @return the photoURL
     */
    public Photo getPhoto() {
        return m_photo;
    }

    /**
     * @param albumName
     *            the albumName to set
     */
    public void setAlbumName(String albumName) {
        m_albumName = albumName;
    }

    /**
     * @param baseFolder
     *            the baseFolder to set
     */
    public void setBaseFolder(String baseFolder) {
        m_baseFolder = baseFolder;
    }

    /**
     * @param photoURL
     *            the photoURL to set
     */
    public void setPhoto(Photo photo) {
        m_photo = photo;
    }

    private File _getFinalFile() {
        return new File(m_baseFolder + File.separator + m_albumName, m_photo.getTitle() + "." + m_photo.getOriginalFormat());
    }

    private File _getTmpFile() {
        return new File(_getFinalFile().getAbsolutePath() + ".tmp");
    }
}
