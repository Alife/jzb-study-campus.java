/**
 * 
 */
package com.jzb.flickr.act;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class GetUserAlbumsAtc extends BaseAction {

    public static final Photoset NOT_IN_ALBUM_PSET;
    static {
        NOT_IN_ALBUM_PSET = new Photoset();
        NOT_IN_ALBUM_PSET.setDescription("Special 'Not In Album PhotoSet' to contain all photos not in a particular album");
        NOT_IN_ALBUM_PSET.setId("NOT_IN_ALBUM_PSET");
        NOT_IN_ALBUM_PSET.setTitle("NOT_IN_ALBUM_PSET");
    }

    private boolean              m_alsoNotInAlbum = true;
    private Pattern              m_regExp         = Pattern.compile(".*", Pattern.CASE_INSENSITIVE);
    private String               m_userID;

    public GetUserAlbumsAtc(ITracer tracer) {
        super(tracer);
    }

    @SuppressWarnings("unchecked")
    public Object execute() throws Exception {

        m_tracer._debug("Getting user albums for: " + m_userID);

        PhotosetsInterface psi = FlickrContext.getFlickr().getPhotosetsInterface();

        // Itera por los albunes del usuario
        Collection<Photoset> col = psi.getList(m_userID).getPhotosets();
        ArrayList<Photoset> albums = new ArrayList<Photoset>();
        for (Photoset pset : col) {
            if (m_regExp.matcher(pset.getTitle()).matches()) {
                albums.add(pset);
            } else {
                m_tracer._debug("  Album filtered: " + pset.getTitle());
            }
        }

        if (m_alsoNotInAlbum && m_regExp.matcher(NOT_IN_ALBUM_PSET.getTitle()).matches()) {
            albums.add(NOT_IN_ALBUM_PSET);
        }

        return albums;
    }

    /**
     * @return the alsoNotInAlbum
     */
    public String getAlsoNotInAlbum() {
        return m_alsoNotInAlbum ? "true" : "false";
    }

    /**
     * @return the regExp
     */
    public String getRegExp() {
        return m_regExp.pattern();
    }

    /**
     * @return the userID
     */
    public String getUserID() {
        return m_userID;
    }

    /**
     * @param alsoNotInAlbum
     *            the alsoNotInAlbum to set
     */
    public void setAlsoNotInAlbum(String alsoNotInAlbum) {
        m_alsoNotInAlbum = Boolean.parseBoolean(alsoNotInAlbum);
    }

    /**
     * @param regExp
     *            the filter to set
     */
    public void setRegExp(String regExp) {
        m_regExp = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
    }

    /**
     * @param userID
     *            the userID to set
     */
    public void setUserID(String userID) {
        m_userID = userID;
    }

}
