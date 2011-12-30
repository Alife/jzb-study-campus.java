/**
 * 
 */
package com.jzb.ipa.bundle;

/**
 * @author n000013
 * 
 */
public interface IBundleDataParser {

    public BundleData parse(byte buffer[]) throws Exception;
}
