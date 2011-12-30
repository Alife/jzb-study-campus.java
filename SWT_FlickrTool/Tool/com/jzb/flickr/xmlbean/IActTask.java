/**
 * 
 */
package com.jzb.flickr.xmlbean;

import org.w3c.dom.Node;

/**
 * @author n000013
 * 
 */
public interface IActTask {

    public boolean canReexecute();

    public boolean canRetry();

    public IActTask clone() throws CloneNotSupportedException;

    /**
     * It is compulsory to return not NULL to keep executing sub-actions
     * @throws Exception
     */
    public void execute() throws Exception;

    public String getActionName();

    public String getActionSignature();

    public void initialize(Node node) throws Exception;

    public void prepareExecution(IAction parentAct, Object parentData) throws Exception;

    public void setActTaskManager(IActTaskManager manager);
}
