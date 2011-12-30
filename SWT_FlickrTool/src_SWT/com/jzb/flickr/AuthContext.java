/**
 * 
 */
package com.jzb.flickr;

/**
 * @author n000013
 * 
 */
public class AuthContext {

    String m_API_Key      = "d184f81aad32a2ffa21b380cab557383";
    String m_AuthToken    = "72157608115575293-e0de90ebd804337b";
    String m_sharedSecret = "4a2bde799880f316";

    /**
     * @return the aPI_Key
     */
    public String getAPI_Key() {
        return m_API_Key;
    }

    /**
     * @return the authToken
     */
    public String getAuthToken() {
        return m_AuthToken;
    }

    /**
     * @return the sharedSecret
     */
    public String getSharedSecret() {
        return m_sharedSecret;
    }

    /**
     * @param aPIKey
     *            the aPI_Key to set
     */
    public void setAPI_Key(String aPIKey) {
        m_API_Key = aPIKey;
    }

    /**
     * @param authToken
     *            the authToken to set
     */
    public void setAuthToken(String authToken) {
        m_AuthToken = authToken;
    }

    /**
     * @param sharedSecret
     *            the sharedSecret to set
     */
    public void setSharedSecret(String sharedSecret) {
        m_sharedSecret = sharedSecret;
    }
}
