/**
 * 
 */
package com.jzb.ipa.bundle;

import java.util.Date;

/**
 * @author n000013
 * 
 */
public final class BundleData {

    public byte[] image;
    public String minOSVersion = "???";
    public String name         = "UNKNOW";
    public String pkgID        = "UNKNOW_" + Math.round(Math.random() * 1000000L);
    public Date   time         = new Date();
    public String version      = "0.0";

    public BundleData() {
    }

    public BundleData(String aName, String aVersion, String aMinOSVer, String aPkgID) {
        name = aName;
        version = aVersion;
        minOSVersion = aMinOSVer;
        pkgID = aPkgID;
    }

    @Override
    public String toString() {
        return "BundleData: name = " + name + ", version  = " + version + ", MinOSVer = " + minOSVersion + ", PkgID = " + pkgID + ", date = " + time;
    }
}
