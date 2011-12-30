/**
 * 
 */
package com.jzb.flickr.act;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.tags.Tag;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class FilterPhotoAct extends BaseAction {

    private Photoset m_album;       // Usado para pasarlo hacia adelante

    private String   m_isFamily;

    private String   m_isFriend;

    private String   m_isPublic;

    private String   m_isUnTagged;

    private Pattern  m_nameRegExp;

    private Photo    m_photo;

    private Pattern  m_tagsRegExp;

    public FilterPhotoAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        m_tracer._debug("Filtering photo to further processing: " + m_photo.getTitle());

        if (_checkPhoto(m_photo)) {
            return m_photo;
        } else {
            m_tracer._debug("  Photo filtered: " + m_photo.getTitle());
            return null;
        }

    }

    /**
     * @return the album
     */
    public Photoset getAlbum() {
        return m_album;
    }


    /**
     * @return the isFamily
     */
    public String getIsFamily() {
        return m_isFamily;
    }

    /**
     * @return the isFriend
     */
    public String getIsFriend() {
        return m_isFriend;
    }

    /**
     * @return the isPublic
     */
    public String getIsPublic() {
        return m_isPublic;
    }

    /**
     * @return the isUnTagged
     */
    public String getIsUnTagged() {
        return m_isUnTagged;
    }

    /**
     * @return the nameRegExp
     */
    public String getNameRegExp() {
        return m_nameRegExp.pattern();
    }

    /**
     * @return the photo
     */
    public Photo getPhoto() {
        return m_photo;
    }

    /**
     * @return the tagsRegExp
     */
    public String getTagsRegExp() {
        return m_tagsRegExp.pattern();
    }

    /**
     * @param album
     *            the album to set
     */
    public void setAlbum(Photoset album) {
        m_album = album;
    }

    /**
     * @param isFamily
     *            the isFamily to set
     */
    public void setIsFamily(String isFamily) {
        m_isFamily = isFamily;
    }

    /**
     * @param isFriend
     *            the isFriend to set
     */
    public void setIsFriend(String isFriend) {
        m_isFriend = isFriend;
    }

    /**
     * @param isPublic
     *            the isPublic to set
     */
    public void setIsPublic(String isPublic) {
        m_isPublic = isPublic;
    }

    /**
     * @param isUnTagged
     *            the isUnTagged to set
     */
    public void setIsUnTagged(String isUnTagged) {
        m_isUnTagged = isUnTagged;
    }

    /**
     * @param nameRegExp
     *            the nameRegExp to set
     */
    public void setNameRegExp(String nameRegExp) {
        m_nameRegExp = Pattern.compile(nameRegExp, Pattern.CASE_INSENSITIVE);
    }

    /**
     * @param photo
     *            the photo to set
     */
    public void setPhoto(Photo photo) {
        m_photo = photo;
    }

    /**
     * @param tagsRegExp
     *            the tagsRegExp to set
     */
    public void setTagsRegExp(String tagsRegExp) {
        m_tagsRegExp = Pattern.compile(tagsRegExp, Pattern.CASE_INSENSITIVE);
    }

    private boolean _bol(String str) {
        return Boolean.parseBoolean(str);
    }

    private boolean _checkPhoto(Photo photo) throws Exception {

        if (m_nameRegExp != null && !m_nameRegExp.matcher(photo.getTitle()).matches()) {
            return false;
        }

        // Chequea que al menos un Tag cumple con la RegExpr
        if (m_tagsRegExp != null) {

            Collection col = photo.getTags();
            if (col == null || col.size() == 0) {
                return false;
            }

            boolean anyTagMaches = false;
            Iterator iter = col.iterator();
            while (iter.hasNext()) {
                Tag tag = (Tag) iter.next();
                // HAY UNA COSA RARA PORQUE TIENE VALUE Y RAW_VALUE??????
                if (m_tagsRegExp.matcher(tag.getValue()).matches()) {
                    // Este lo ha pasado...sigue al siguiente filtro
                    anyTagMaches = true;
                    break;
                }
            }

            if (!anyTagMaches)
                return false;
        }

        if (m_isFamily != null && _bol(m_isFamily) != photo.isFamilyFlag()) {
            return false;
        }

        if (m_isFriend != null && _bol(m_isFriend) != photo.isFriendFlag()) {
            return false;
        }

        if (m_isPublic != null && _bol(m_isPublic) != photo.isPublicFlag()) {
            return false;
        }

        if (m_isUnTagged != null && _bol(m_isUnTagged)) {
            Collection col = photo.getTags();
            return (col == null || col.size() == 0);
        }

        return true;
    }
}
