/**
 * 
 */
package com.jzb.kk.pp;

/**
 * @author n63636
 * 
 */
public class BBInfo {

    public boolean isOK = false;
    public String  pageURL;
    public String  zone;
    public String  roomsNumber;
    public String  dayMinPrice;
    public String  dayMaxPrice;
    public String  extraBedPrice;
    public double  gpsLat;
    public double  gpsLng;

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "isOk='" + isOK + "' roomsNumber='" + roomsNumber + "' extraBedPrice='" + extraBedPrice + "' dayMinPrice='" + dayMinPrice + "' dayMaxPrice='" + dayMaxPrice + "' gpsLat='" + gpsLat
                + "' gpsLng='" + gpsLng + "' zone='" + zone + "' pageURL='" + pageURL + "'";
    }

    public String toExcel() {
        return "" + isOK + ", " + roomsNumber + ", " + extraBedPrice + ", " + dayMinPrice + ", " + dayMaxPrice + ", " + gpsLat + ", " + gpsLng + ", " + zone + ", " + pageURL + "";
    }
}
