/**
 * 
 */
package com.jzb.t2;

/**
 * @author n000013
 * 
 */
public interface ITracer {

    public void _debug(String msg);

    public void _error(String msg);

    public void _error(String msg, Throwable th);

    public void _info(String msg);

    public void _warn(String msg);

    public void _warn(String msg, Throwable th);
}
