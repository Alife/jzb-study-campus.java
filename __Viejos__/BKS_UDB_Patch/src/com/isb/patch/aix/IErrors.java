/**
 * 
 */
package com.isb.patch.aix;

/**
 * This interface contains all the error codes that the application could show to the user. No <i>sensible</i> information is shown.
 * <p>
 * If the error code root is like <b>0xF000????</b> it means that is was a severe error.
 * If it is like <b>0x0000????</b> is was normal and WSAD will continue working
 * <p>
 * Additional information will be stored in the trace file.
 * 
 * @author IS201105
 * 
 */
public interface IErrors {

    // General error. Application couldn't even be launched
    public static final String ERROR_LAUNCHING_APPLICATION       = "0xF000FFFF";

    // Common errors for all the Updating Steps
    public static final String ERROR_WSAD_FOLDER_NOT_FOUND       = "0x00001001";
    public static final String ERROR_READING_PROPERTIES_BUNDLE   = "0x00001002";

    // Error related with Vega Updating Step
    public static final String ERROR_RENAMING_VEGA_PROPERTIES_1  = "0x00002001";
    public static final String ERROR_RENAMING_VEGA_PROPERTIES_2  = "0xF0002002";
    public static final String ERROR_WRITING_VEGA_PROPERTIES     = "0x00002003";

    // Error related with Deneb Updating Step
    public static final String ERROR_RENAMING_DENEB_PROPERTIES_1 = "0x00003001";
    public static final String ERROR_RENAMING_DENEB_PROPERTIES_2 = "0xF0003002";
    public static final String ERROR_WRITING_DENEB_PROPERTIES    = "0x00003003";

}
