/**
 * 
 */
package com.jzb.flickr;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * @author n000013
 * 
 */
public class UploadInfo {

    private boolean           m_async       = false;
    private boolean           m_isFamily    = true;
    private boolean           m_isFriend    = true;
    private boolean           m_isHidden    = true;
    private boolean           m_isPublic    = false;

    private int               m_safetyLevel = 3;

    private ArrayList<String> m_tags        = new ArrayList<String>();

    private String            m_title;

    public UploadInfo(String title) {
        m_title = title;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void addTags(String ... tags) {
        if (tags != null) {
            for (String s : tags)
                m_tags.add(s);
        }
    }

    public TreeMap<String, String> getParams() {

        TreeMap<String, String> params = new TreeMap<String, String>();

        params.put("async", m_async ? "1" : "0");

        params.put("is_family", m_isFamily ? "1" : "0");
        params.put("is_friend", m_isFriend ? "1" : "0");
        params.put("is_public", m_isPublic ? "1" : "0");

        params.put("safety_level", Integer.toString(m_safetyLevel));

        params.put("content_type", "1");

        params.put("hidden", m_isHidden ? "2" : "1");

        params.put("tags", _getTagsAsString());

        params.put("title", m_title);

        return params;
    }

    /**
     * @return the safetyLevel
     */
    public int getSafetyLevel() {
        return m_safetyLevel;
    }

    /**
     * @return the tags
     */
    public ArrayList<String> getTags() {
        return m_tags;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @return the async
     */
    public boolean isAsync() {
        return m_async;
    }

    /**
     * @return the isFamily
     */
    public boolean isFamily() {
        return m_isFamily;
    }

    /**
     * @return the isFriend
     */
    public boolean isFriend() {
        return m_isFriend;
    }

    /**
     * @return the isHidden
     */
    public boolean isHidden() {
        return m_isHidden;
    }

    /**
     * @return the isPublic
     */
    public boolean isPublic() {
        return m_isPublic;
    }

    /**
     * @param async
     *            the async to set
     */
    public void setAsync(boolean async) {
        m_async = async;
    }

    /**
     * @param isHidden
     *            the isHidden to set
     */
    public void setHidden(boolean isHidden) {
        m_isHidden = isHidden;
    }

    /**
     * @param isFamily
     *            the isFamily to set
     */
    public void setIsFamily(boolean isFamily) {
        m_isFamily = isFamily;
    }

    /**
     * @param isFriend
     *            the isFriend to set
     */
    public void setIsFriend(boolean isFriend) {
        m_isFriend = isFriend;
    }

    /**
     * @param isPublic
     *            the isPublic to set
     */
    public void setIsPublic(boolean isPublic) {
        m_isPublic = isPublic;
    }

    /**
     * @param safetyLevel
     *            the safetyLevel to set
     */
    public void setSafetyLevel(int safetyLevel) {

        if (safetyLevel < 1 || safetyLevel > 3)
            throw new IllegalArgumentException("safetyLevel must be between 1 and 3");

        m_safetyLevel = safetyLevel;
    }

    /**
     * @param tags
     *            the tags to set
     */
    public void setTags(String ... tags) {
        m_tags.clear();
        addTags(tags);
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        m_title = title;
    }

    private String _getTagsAsString() {

        StringBuffer sb = new StringBuffer();

        boolean first = true;
        for (String tag : m_tags) {
            if (!first)
                sb.append(" ");
            sb.append('"').append(tag).append('"');
            first = false;
        }

        return sb.toString();
    }
}
