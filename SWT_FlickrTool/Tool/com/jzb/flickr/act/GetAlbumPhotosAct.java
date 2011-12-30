/**
 * 
 */
package com.jzb.flickr.act;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.photos.Extras;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotoList;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photosets.Photoset;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class GetAlbumPhotosAct extends BaseAction {

    private Photoset m_album;

    public GetAlbumPhotosAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        m_tracer._debug("Getting photos from album: " + m_album.getTitle());

        ArrayList<Photo> photos;
        if (!m_album.getTitle().equals("NOT_IN_ALBUM_PSET"))
            photos = _getNormalAlbumPhotos();
        else
            photos = _getNotInAlbumPhotos();

        return photos;
    }

    /**
     * @return the album
     */
    public Photoset getAlbum() {
        return m_album;
    }

    /**
     * @param album
     *            the album to set
     */
    public void setAlbum(Photoset album) {
        m_album = album;
    }

    private ArrayList<Photo> _getNormalAlbumPhotos() throws Exception {

        PhotosetsInterface psi = FlickrContext.getFlickr().getPhotosetsInterface();

        Set MY_EXTRAS = new HashSet();
        MY_EXTRAS.add(Extras.ORIGINAL_FORMAT);
        MY_EXTRAS.add(Extras.OWNER_NAME);
        MY_EXTRAS.add(Extras.TAGS);
        MY_EXTRAS.add("visibility"); // Necesario para el tema de "public", "friend" y "family"
        // MY_EXTRAS.add(Extras.MACHINE_TAGS); // Esto incluye cosas como la GEO-REF
        // MY_EXTRAS.add(Extras.DATE_TAKEN);
        // MY_EXTRAS.add(Extras.DATE_UPLOAD);
        // MY_EXTRAS.add(Extras.ICON_SERVER);
        // MY_EXTRAS.add(Extras.LAST_UPDATE);
        // MY_EXTRAS.add(Extras.LICENSE);
        // MY_EXTRAS.add(Extras.GEO);

        ArrayList<Photo> photos = new ArrayList<Photo>();
        int pageNumber = 1, total = 0;
        while (total < m_album.getPhotoCount()) {
            PhotoList plist = psi.getPhotos(m_album.getId(), MY_EXTRAS, Flickr.PRIVACY_LEVEL_NO_FILTER, 100, pageNumber);
            for (int n = 0; n < plist.size(); n++) {
                Photo photo = (Photo) plist.get(n);
                photos.add(photo);
            }
            total += plist.size();
            pageNumber++;
        }

        return photos;
    }

    private ArrayList<Photo> _getNotInAlbumPhotos() throws Exception {

        Set MY_EXTRAS = new HashSet();
        MY_EXTRAS.add(Extras.ORIGINAL_FORMAT);
        MY_EXTRAS.add(Extras.OWNER_NAME);
        MY_EXTRAS.add(Extras.TAGS);
        MY_EXTRAS.add("visibility"); // Necesario para el tema de "public", "friend" y "family"
        // MY_EXTRAS.add(Extras.MACHINE_TAGS); // Esto incluye cosas como la GEO-REF
        // MY_EXTRAS.add(Extras.DATE_TAKEN);
        // MY_EXTRAS.add(Extras.DATE_UPLOAD);
        // MY_EXTRAS.add(Extras.ICON_SERVER);
        // MY_EXTRAS.add(Extras.LAST_UPDATE);
        // MY_EXTRAS.add(Extras.LICENSE);
        // MY_EXTRAS.add(Extras.GEO);

        PhotosInterface photoInt = FlickrContext.getFlickr().getPhotosInterface();
        ArrayList<Photo> photos = new ArrayList<Photo>();
        int pageNumber = 1, total = 0;
        while (true) {
            PhotoList plist = photoInt.getNotInSet(100, pageNumber);
            for (int n = 0; n < plist.size(); n++) {
                Photo photo = (Photo) plist.get(n);
                photos.add(photo);
            }
            total += plist.size();
            pageNumber++;
            if (plist.size() < 100)
                break;
        }

        return photos;
    }
}
