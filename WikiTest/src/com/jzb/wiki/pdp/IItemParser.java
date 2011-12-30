/**
 * 
 */
package com.jzb.wiki.pdp;

/**
 * @author n000013
 * 
 */
public interface IItemParser {

    public boolean accept(String line);

    public IPdPItem parse() throws Exception;
}