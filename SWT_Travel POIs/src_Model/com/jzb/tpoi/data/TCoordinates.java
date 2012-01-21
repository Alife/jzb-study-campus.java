/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.StringTokenizer;

/**
 * @author n63636
 * 
 */
public class TCoordinates implements Externalizable {

    public static double DEFAULT_ALT = 0.0;
    public static double DEFAULT_LAT = 0.0;
    public static double DEFAULT_LNG = 0.0;

    private double       m_alt;
    private double       m_lat;
    private double       m_lng;

    // ---------------------------------------------------------------------------------
    public TCoordinates() {
        m_lat = DEFAULT_LAT;
        m_lng = DEFAULT_LNG;
        m_alt = DEFAULT_ALT;
    }

    // ---------------------------------------------------------------------------------
    public TCoordinates(double lat, double lng) {
        this(lat, lng, 0.0);
    }

    // ---------------------------------------------------------------------------------
    public TCoordinates(double lat, double lng, double alt) {
        m_lat = lat;
        m_lng = lng;
        m_alt = alt;
    }

    // ---------------------------------------------------------------------------------
    public TCoordinates(String str) {
        if (str == null || str.trim().length() == 0) {
            m_lat = DEFAULT_LAT;
            m_lng = DEFAULT_LNG;
            m_alt = DEFAULT_ALT;
        } else {
            StringTokenizer st = new StringTokenizer(str, ",");
            m_lat = Double.parseDouble(st.nextToken());
            m_lng = Double.parseDouble(st.nextToken());
            m_alt = Double.parseDouble(st.nextToken());
        }
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the alt
     */
    public double getAlt() {
        return m_alt;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the lat
     */
    public double getLat() {
        return m_lat;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the lng
     */
    public double getLng() {
        return m_lng;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        m_lat = in.readDouble();
        m_lng = in.readDouble();
        m_alt = in.readDouble();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return m_lat + "," + m_lng + "," + m_alt;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(m_lat);
        out.writeDouble(m_lng);
        out.writeDouble(m_alt);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param alt
     *            the alt to set
     */
    private void setAlt(double alt) {
        m_alt = alt;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param lat
     *            the lat to set
     */
    private void setLat(double lat) {
        m_lat = lat;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param lng
     *            the lng to set
     */
    private void setLng(double lng) {
        m_lng = lng;
    }

}