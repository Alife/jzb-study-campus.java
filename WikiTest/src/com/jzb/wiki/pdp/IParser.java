/**
 * 
 */
package com.jzb.wiki.pdp;

/**
 * @author n000013
 * 
 */
public interface IParser {

    public String readLine() throws Exception;

    public void backLine() throws Exception;

    public IItemParser[] getItemParsers();
}
