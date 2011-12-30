/**
 * 
 */
package com.jzb.flickr.act;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.StringTokenizer;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.uploader.UploadMetaData;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class UploadPhotoAct extends BaseAction {

    private boolean         m_folderAsTag;
    private File            m_photoFile;
    private HashSet<String> m_tags = new HashSet<String>();

    public UploadPhotoAct(ITracer tracer) {
        super(tracer);
    }

    @Override
    public boolean canReexecute() {
        return false;
    }

    @Override
    public boolean canRetry() {
        return false;
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        m_tracer._debug("Uploading photo: " + m_photoFile);

        UploadMetaData umd = new UploadMetaData();
        umd.setAsync(false);
        umd.setContentType(Flickr.CONTENTTYPE_PHOTO);

        umd.setFamilyFlag(true);
        umd.setFriendFlag(true);
        umd.setHidden(true);
        umd.setSafetyLevel(Flickr.SAFETYLEVEL_SAFE);

        umd.setTitle(_getNameWithoutExt());

        if (m_folderAsTag)
            m_tags.add("\""+m_photoFile.getParentFile().getName()+"\"");
        umd.setTags(m_tags);

        long t1 = System.currentTimeMillis();
        String photoId = FlickrContext.getFlickr().getUploader(10, 600).upload(new FileInputStream(m_photoFile), umd);
        long t2 = System.currentTimeMillis();
        m_tracer._debug("Uploaded photo[" + (t2 - t1) + " ms | " + photoId + "]: " + m_photoFile);

        // FlickrContext.getFlickr().getUploadInterface().checkTickets();
        return photoId;
    }

    /**
     * @return the folderAsAlbum
     */
    public String getFolderAsTag() {
        return m_folderAsTag ? "true" : "false";
    }

    /**
     * @return the photoFile
     */
    public File getPhotoFile() {
        return m_photoFile;
    }

    /**
     * @return the photoFileName
     */
    public String getPhotoFileName() {
        return m_photoFile.getAbsolutePath();
    }

    /**
     * @return the tags
     */
    public String getTags() {
        String cad = "";
        boolean first = true;
        for (String s : m_tags) {
            if (!first)
                cad += ", ";
            cad += s;
            first = false;
        }
        return cad;
    }

    /**
     * @param folderAsAlbum
     *            the folderAsAlbum to set
     */
    public void setFolderAsTag(String folderAsTag) {
        m_folderAsTag = Boolean.parseBoolean(folderAsTag);
    }

    /**
     * @param photoFile
     *            the photoFile to set
     */
    public void setPhotoFile(File photoFile) {
        m_photoFile = photoFile;
    }

    /**
     * @param photoFile
     *            the photoFileName to set
     */
    public void setPhotoFileName(String fullFilePath) {
        m_photoFile = new File(fullFilePath);
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(String tags) {
        StringTokenizer st = new StringTokenizer(tags, ",");
        while (st.hasMoreTokens()) {
            m_tags.add("\"" + st.nextToken().trim() + "\"");
        }
    }

    private String _getNameWithoutExt() {
        int pos = m_photoFile.getName().lastIndexOf('.');
        if (pos != -1)
            return m_photoFile.getName().substring(0, pos);
        else
            return m_photoFile.getName();
    }
}
