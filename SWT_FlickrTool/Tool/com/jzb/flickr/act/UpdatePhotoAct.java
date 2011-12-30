/**
 * 
 */
package com.jzb.flickr.act;

import java.util.HashSet;
import java.util.StringTokenizer;
import com.aetrion.flickr.photos.Permissions;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotosInterface;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class UpdatePhotoAct extends BaseAction {

    private String          m_description;

    private Boolean         m_isFamily;

    private Boolean         m_isFriend;

    private Boolean         m_isPublic;

    private Photo           m_photo;

    private boolean         m_replaceTags = false;

    private HashSet<String> m_tags = new HashSet<String>();

    private String          m_title;

    public UpdatePhotoAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        m_tracer._debug("Updating photo info: " + m_photo.getTitle());

        PhotosInterface pi = FlickrContext.getFlickr().getPhotosInterface();

        // Update tags if required
        if (m_tags.size() > 0) {
            String tags[] = m_tags.toArray(new String[m_tags.size()]);
            if (m_replaceTags) {
                pi.setTags(m_photo.getId(), tags);
            } else {
                pi.addTags(m_photo.getId(), tags);
            }
        }

        // Updates title and description
        if (m_title != null || m_description != null) {
            Photo photoInfo = pi.getPhoto(m_photo.getId());
            String title = m_title != null ? m_title : photoInfo.getTitle();
            String description = m_description != null ? m_description : photoInfo.getDescription();
            pi.setMeta(m_photo.getId(), title, description);
        }

        // Updates perms
        if (m_isFamily != null || m_isFriend != null || m_isPublic != null) {
            Permissions perms = pi.getPerms(m_photo.getId());
            if (m_isFamily != null)
                perms.setFamilyFlag(m_isFamily);
            if (m_isFriend != null)
                perms.setFriendFlag(m_isFriend);
            if (m_isPublic != null)
                perms.setPublicFlag(m_isPublic);
            pi.setPerms(m_photo.getId(), perms);
        }

        return null;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @return the isFamily
     */
    public String getIsFamily() {
        return m_isFamily.toString();
    }

    /**
     * @return the isFriend
     */
    public String getIsFriend() {
        return m_isFriend.toString();
    }

    /**
     * @return the isPublic
     */
    public String getIsPublic() {
        return m_isPublic.toString();
    }

    /**
     * @return the photo
     */
    public Photo getPhoto() {
        return m_photo;
    }

    /**
     * @return the replaceTags
     */
    public String getReplaceTags() {
        return m_replaceTags ? "true" : "false";
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
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        m_description = description;
    }

    /**
     * @param isFamily
     *            the isFamily to set
     */
    public void setIsFamily(String isFamily) {
        m_isFamily = Boolean.parseBoolean(isFamily);
    }

    /**
     * @param isFriend
     *            the isFriend to set
     */
    public void setIsFriend(String isFriend) {
        m_isFriend = Boolean.parseBoolean(isFriend);
    }

    /**
     * @param isPublic
     *            the isPublic to set
     */
    public void setIsPublic(String isPublic) {
        m_isPublic = Boolean.parseBoolean(isPublic);
    }

    /**
     * @param photo
     *            the photo to set
     */
    public void setPhoto(Photo photo) {
        m_photo = photo;
    }

    /**
     * @param replaceTags
     *            the replaceTags to set
     */
    public void setReplaceTags(String replaceTags) {
        m_replaceTags = Boolean.parseBoolean(replaceTags);
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(String tags) {
        StringTokenizer st = new StringTokenizer(tags, ",");
        while (st.hasMoreTokens()) {
            // m_tags.add("\"" + st.nextToken().trim() + "\"");
            m_tags.add(st.nextToken().trim());
        }
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        m_title = title;
    }

}
