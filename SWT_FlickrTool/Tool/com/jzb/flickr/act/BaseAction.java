/**
 * 
 */
package com.jzb.flickr.act;

import java.lang.reflect.Method;
import com.jzb.flickr.xmlbean.IAction;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public abstract class BaseAction implements IAction {

    protected ITracer m_tracer;

    public BaseAction(ITracer tracer) {
        m_tracer = tracer;
    }

    public boolean canReexecute() {
        return true;
    }

    public boolean canRetry() {
        return true;
    }

    public String getSignature() {

        StringBuffer sb = new StringBuffer();

        sb.append(getClass().getName());
        sb.append("{ ");

        Method mList[] = getClass().getDeclaredMethods();
        for (Method m : mList) {
            if (m.getReturnType().equals(String.class) && m.getParameterTypes().length == 0) {
                sb.append(m.getName());
                sb.append("=");
                String sVal;
                try {
                    sVal = (String) m.invoke(this);
                } catch (Exception ex) {
                    sVal = "*Exception*";
                }

                if (sVal != null)
                    sb.append(sVal);
                else
                    sb.append("");
                sb.append(" | ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

}
