/**
 * 
 */
package com.jzb.flickr.act;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.people.User;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class GetUserInfoAct extends BaseAction {

    private boolean m_byEmail;
    private String  m_userName;

    public GetUserInfoAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        m_tracer._debug("Getting user info for: " + m_userName);
        Flickr flickr = FlickrContext.getFlickr();
        User user;
        if (m_byEmail)
            user = flickr.getPeopleInterface().findByEmail(m_userName);
        else
            user = flickr.getPeopleInterface().findByUsername(m_userName);

        return user;

    }

    /**
     * @return the byeEmail
     */
    public String getByEmail() {
        return m_byEmail ? "true" : "false";
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return m_userName;
    }

    /**
     * @param byeEmail
     *            the byeEmail to set
     */
    public void setByEmail(String byEmail) {
        m_byEmail = Boolean.parseBoolean(byEmail);
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        m_userName = userName;
    }
}
