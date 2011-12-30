/**
 * 
 */
package com.jzb.flickr.xmlbean;

/**
 * @author n000013
 * 
 */
public interface IAction {

    public boolean canReexecute();

    public boolean canRetry();

    public Object execute() throws Exception;

    public String getSignature();
}
