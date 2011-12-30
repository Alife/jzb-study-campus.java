/**
 * 
 */
package com.jzb.bc.loaders;

import java.io.File;

/**
 * @author n63636
 * 
 */
public interface ILoader {

    public void load(File prjFolder, File resource) throws Exception;

    public void load(Object param, File prjFolder, File resource) throws Exception;

}
