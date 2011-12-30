/**
 * 
 */
package com.jzb.flickr.act;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.aetrion.flickr.Flickr;
import com.aetrion.flickr.REST;
import com.aetrion.flickr.RequestContext;
import com.aetrion.flickr.auth.Auth;
import com.aetrion.flickr.auth.Permission;
import com.aetrion.flickr.util.IOUtilities;

/**
 * @author n000013
 * 
 */
public class FlickrContext {

    private static HashMap<String, Object> m_data = new HashMap<String, Object>();
    private static boolean                 m_hasFailed;
    private static String                  s_apiKey;

    private static ThreadLocal<Flickr>     s_flickr;
    private static String                  s_secret;

    private static String                  s_token;

    
    public static Object getCtxData(String key) throws Exception {
        return m_data.get(key);
    }

    
    public static Flickr getFlickr() throws Exception {
        Flickr obj = s_flickr.get();
        if (obj == null) {
            obj = _initSession();
            s_flickr.set(obj);
        }
        return obj;
    }

    /**
     * @return the m_hasFailed
     */
    public static boolean hasFailed() {
        return m_hasFailed;
    }

    public static void init() throws Exception {

        s_flickr = new ThreadLocal<Flickr>();

        InputStream in = null;

        try {
            in = FlickrContext.class.getResourceAsStream("/flickrSetup.properties");
            if (in == null) {
                throw new IOException("Resource 'flickrSetup.properties' cannot be found in base folder");
            }
            Properties properties = new Properties();
            properties.load(in);

            boolean dbg1 = "true".equalsIgnoreCase(properties.getProperty("traceDebug", "false"));
            boolean dbg2 = "true".equalsIgnoreCase(properties.getProperty("debugReqRsp", "false"));

            Flickr.tracing = dbg1;
            Flickr.debugRequest = dbg2;
            Flickr.debugStream = dbg2;

            String proxy = properties.getProperty("proxy");
            if (proxy != null) {
                int pos = proxy.indexOf(":");
                System.setProperty("http.proxyHost", proxy.substring(0, pos));
                System.setProperty("http.proxyPort", proxy.substring(pos + 1));
            }

            s_apiKey = properties.getProperty("apiKey");
            s_secret = properties.getProperty("secret");
            s_token = properties.getProperty("token");

        } finally {
            IOUtilities.close(in);
        }

    }

    public static Object setCtxData(String key, Object value) throws Exception {
        return m_data.put(key, value);
    }

    /**
     * @param failed the m_hasFailed to set
     */
    public static void setHasFailed(boolean failed) {
        m_hasFailed = failed;
    }

    private static Flickr _initSession() throws Exception {

        Flickr flickr = new Flickr(s_apiKey, s_secret, new REST(3, 10, 10));

        Auth auth = new Auth();
        auth.setPermission(Permission.DELETE);
        auth.setToken(s_token);

        RequestContext requestContext = RequestContext.getRequestContext();
        requestContext.setAuth(auth);

        return flickr;

    }
}
