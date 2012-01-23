/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.swt.graphics.Image;

/**
 * @author n63636
 * 
 */
public class TIcon {

    public static final String            DEFAULT_CAT_ICON_URL   = "http://maps.gstatic.com/mapfiles/ms2/micons/landmarks-jp.png";
    public static final String            DEFAULT_MAP_ICON_URL   = "http://maps.gstatic.com/mapfiles/ms2/micons/POI.png";
    public static final String            DEFAULT_POINT_ICON_URL = "http://maps.gstatic.com/mapfiles/ms2/micons/blue-dot.png";
    public static final String            EIP_DEF_ICON_URL       = "http://maps.gstatic.com/mapfiles/ms2/micons/earthquake.png";
    private static final String           BASE_ICONS_URL         = "http://maps.gstatic.com/mapfiles/ms2/micons/";
    private static final int              BASE_ICONS_URL_LEN     = BASE_ICONS_URL.length();
    private static final String           BASE_ICONS_URL2        = "http://maps.google.com/mapfiles/ms/micons/";

    private static final int              BASE_ICONS_URL2_LEN    = BASE_ICONS_URL2.length();
    private static HashMap<String, TIcon> m_names                = new HashMap<String, TIcon>();
    private static HashMap<String, TIcon> m_urls                 = new HashMap<String, TIcon>();
    private static TIcon                  s_unknownIcon;

    private static final String           UNKNOW_ICON_FILE       = "C:\\JZarzuela\\_git_repos\\java-campus\\SWT_Travel POIs\\resources\\icons\\Error-icon.png";
    private static final String           UNKNOW_ICON_NAME       = "caution";
    private static final String           UNKNOW_ICON_URL        = "http://maps.gstatic.com/mapfiles/ms2/micons/caution.png";

    // ---------------------------------------------------------------------------------
    static {
        _initialize();
    }
    private Image                         m_image;
    private String                        m_name;

    private String                        m_url;

    // ---------------------------------------------------------------------------------
    public static TIcon _getDefault() {
        return s_unknownIcon;
    }

    // ---------------------------------------------------------------------------------
    public static void _initialize() {

        m_urls.clear();
        m_names.clear();

        Image uimg = new Image(null, UNKNOW_ICON_FILE);
        s_unknownIcon = new TIcon(UNKNOW_ICON_NAME, UNKNOW_ICON_URL, uimg);

        File iconsBaseFolder = new File("C:\\JZarzuela\\_git_repos\\java-campus\\SWT_Travel POIs\\resources\\icons\\gmaps");
        for (File ficon : iconsBaseFolder.listFiles()) {

            if (ficon.getName().toLowerCase().contains("shadow")) {
                continue;
            }

            String name = ficon.getName().substring(0, ficon.getName().length());
            String url = BASE_ICONS_URL + ficon.getName();
            Image img = new Image(null, ficon.getAbsolutePath());

            TIcon icon = new TIcon(name, url, img);

            m_names.put(name.toLowerCase(), icon);
            m_urls.put(url.toLowerCase(), icon);
        }
    }

    // ---------------------------------------------------------------------------------
    public static Collection<TIcon> allIcons() {
        return m_urls.values();
    }

    // ---------------------------------------------------------------------------------
    public static TIcon createFromName(String name) {
        TIcon icon = m_names.get(name.toLowerCase());
        if (icon != null) {
            return icon;
        } else {
            TIcon unknown = new TIcon(name, "@unknown://ulr/" + name, s_unknownIcon.getImage());
            m_names.put(unknown.getName(), unknown);
            m_urls.put(unknown.getUrl(), unknown);
            return unknown;
        }
    }

    // ---------------------------------------------------------------------------------
    public static TIcon createFromSmallURL(String url) {
        if (url.startsWith("@@")) {
            url = BASE_ICONS_URL + url.substring(2);
        }
        return createFromURL(url);
    }

    // ---------------------------------------------------------------------------------
    public static TIcon createFromURL(String url) {

        if (url.startsWith(BASE_ICONS_URL2)) {
            url = BASE_ICONS_URL + url.substring(BASE_ICONS_URL2_LEN);
        }

        TIcon icon = m_urls.get(url.toLowerCase());
        if (icon != null) {
            return icon;
        } else {
            TIcon unknown = new TIcon("unknownName", url, s_unknownIcon.getImage());
            m_names.put(unknown.getName(), unknown);
            m_urls.put(unknown.getUrl(), unknown);
            return unknown;
        }
    }

    // ---------------------------------------------------------------------------------
    private TIcon(String name, String url, Image image) {
        m_name = name;
        m_url = url;
        m_image = image;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(this.getClass()) && m_url.equals(((TIcon) obj).m_url);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the image
     */
    public Image getImage() {
        return m_image;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    // ---------------------------------------------------------------------------------
    public String getSmallUrl() {
        if (m_url.startsWith(BASE_ICONS_URL))
            return "@@" + m_url.substring(BASE_ICONS_URL_LEN);
        else
            return m_url;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return m_url;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "name = " + m_name + " url = " + m_url;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param image
     *            the image to set
     */
    private void setImage(Image image) {
        m_image = image;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param name
     *            the name to set
     */
    private void setName(String name) {
        m_name = name;
    }

    /**
     * @param url
     *            the url to set
     */
    private void setUrl(String url) {
        m_url = url;
    }

}
