/**
 * 
 */
package com.jzb.tpoi.data;

/**
 * @author n63636
 * 
 */
public class GMercatorProjection {

    public static final int MAP_SIZE = 1000;

    public static double lngToX(double lng) {
        return ((lng + 180) / 360 * MAP_SIZE);
    }

    public static double latToY(double lat) {
        double sinLatitude = Math.sin(lat * Math.PI / 180);
        return ((0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI)) * MAP_SIZE);
    }

    public static double XToLng(double x) {
        return 360 * ((x / MAP_SIZE) - 0.5);
    }

    public static double YToLat(double y) {
        double yy = 0.5 - (y / MAP_SIZE);
        return 90 - 360 * Math.atan(Math.exp(-yy * 2 * Math.PI)) / Math.PI;
    }

}
