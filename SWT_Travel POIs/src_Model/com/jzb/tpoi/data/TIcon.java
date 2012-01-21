/**
 * 
 */
package com.jzb.tpoi.data;

/**
 * @author n63636
 * 
 */
public class TIcon {

    private static String     NAME_ERRONEUS_ICON = "Icono para Error";
    public static final TIcon ERRONEUS_ICON      = createFromName(NAME_ERRONEUS_ICON);

    private String            m_name;

    private String            m_url;

    // ---------------------------------------------------------------------------------
    public static TIcon createFromName(String name) {
        return new TIcon(name, "calcURL");
    }

    // ---------------------------------------------------------------------------------
    public static TIcon createFromURL(String url) {
        return new TIcon("calcName", url);
    }

    // ---------------------------------------------------------------------------------
    public TIcon(String name, String url) {
        m_name = name;
        m_url = url;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the name
     */
    public String getName() {
        return m_name;
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
