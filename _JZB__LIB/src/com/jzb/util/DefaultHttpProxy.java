/**
 * 
 */
package com.jzb.util;

import java.net.InetSocketAddress;
import java.net.Socket;

import HTTPClient.AuthorizationInfo;
import HTTPClient.HTTPConnection;

/**
 * @author n63636
 * 
 */
public class DefaultHttpProxy {

    private static AppPreferences s_prefs = new AppPreferences("DefaultHttpProxy");

    public static void setDefaultProxy() {

        try {
            Tracer._debug("Checking connection with default proxy");

            s_prefs.load(true);

            String proxyHost = s_prefs.getPref("ProxyHost");
            int proxyPort = (int) s_prefs.getPrefLong("ProxyPort", -1);
            String userName = _getClearText(s_prefs.getPref("user"));
            String userPwd = _getClearText(s_prefs.getPref("pwd"));
            String realm = s_prefs.getPref("realm");

            if (proxyHost == null || proxyPort == -1) {
                Tracer._error("Preferences file doesn't exist or has invalid data: " + s_prefs.getPrefFile());

                s_prefs.setPref("ProxyHost", s_prefs.getPref("ProxyHost", "unknown"));
                s_prefs.setPref("ProxyPort", s_prefs.getPref("ProxyPort", "-1"));
                s_prefs.setPref("user", s_prefs.getPref("user", "cypheredUserName"));
                s_prefs.setPref("pwd", s_prefs.getPref("pwd", "cypheredUserPwd"));
                s_prefs.setPref("realm", s_prefs.getPref("realm", "a"));
                s_prefs.save();
                return;
            }

            
            Socket cs = new Socket();
            cs.connect(new InetSocketAddress(proxyHost, proxyPort),200);
            cs.close();

            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", String.valueOf(proxyPort));
            System.setProperty("https.proxyHost", proxyHost);
            System.setProperty("https.proxyPort", String.valueOf(proxyPort));

            HTTPConnection.setProxyServer(proxyHost, proxyPort);
            AuthorizationInfo.addBasicAuthorization(proxyHost, proxyPort, realm, userName, userPwd);

            Tracer._debug("Connection checked!. Going through default proxy.");

        } catch (Exception ex) {
            Tracer._warn("Cannot connect with the proxy. Using direct connection...");
        }
    }

    private static String _getClearText(String cad) {

        String msg = "";

        int n = 0;
        while (n < cad.length()) {
            String h = "0x";
            h += cad.charAt(n++);
            h += cad.charAt(n++);
            int i = (Integer.decode(h).intValue() ^ 0x00000055) & 0x000000FF;
            char c = (char) i;
            msg += c;
        }

        return msg;
    }

    @SuppressWarnings("unused")
    private static String _getEncodedText(String cad) {

        String msg = "";
        int n = 0;
        while (n < cad.length()) {
            char c = cad.charAt(n++);
            int i = (((int) c) & 0x000000FF) ^ 0x00000055;
            if (i < 10)
                msg += '0';
            msg += Integer.toHexString(i);
        }
        return msg;

    }

}
