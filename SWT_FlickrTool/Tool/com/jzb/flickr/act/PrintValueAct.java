/**
 * 
 */
package com.jzb.flickr.act;

import java.io.File;

import com.aetrion.flickr.photos.Photo;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class PrintValueAct extends BaseAction {

    private Object m_value;

    public PrintValueAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        if (m_value instanceof Photo) {
            Photo photo = (Photo) m_value;
            m_tracer._info("** Print value PHOTO: title='" + photo.getTitle() + "' id='" + photo.getId() + "'");
        } else if (m_value instanceof File) {
            File file = (File) m_value;
            m_tracer._info("** Print value FILE: name='" + file.getName() + "', path='" + file.getAbsolutePath() + "'");
        } else {
            m_tracer._info("** Print value: '" + m_value + "'");
        }
        return null;

    }

    /**
     * @return the value
     */
    public Object getValue() {
        return m_value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Object value) {
        m_value = value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(Photo value) {
        m_value = value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(File value) {
        m_value = value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        m_value = value;
    }
}
