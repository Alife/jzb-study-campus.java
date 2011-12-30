/**
 * 
 */
package com.jzb.at.api;

import java.net.URL;

import org.json.JSONObject;

import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Tracer;

import HTTPClient.HTTPConnection;
import HTTPClient.HTTPResponse;
import HTTPClient.NVPair;

/**
 * @author n63636
 * 
 */
public class AppTrkrAPI {

    private static final int SOCKET_TIMEOUT = 30000;

    private boolean          m_traceOn;

    public AppTrkrAPI() {
        this(false);
    }

    public AppTrkrAPI(boolean traceOn) {
        m_traceOn = traceOn;
        DefaultHttpProxy.setDefaultProxy();
    }

    public JSONObject getItunesIDs(String... bundleNames) throws Exception {

        JSONObject req2 = new JSONObject();
        req2.put("bundleList", bundleNames);

        JSONObject req = new JSONObject();
        req.put("object", "Bundle");
        req.put("action", "getItunesIDs");
        req.put("args", req2);

        JSONObject rsp = _api_call(req);

        return rsp;
    }

    public JSONObject getAppDetails(long ID) throws Exception {

        JSONObject req2 = new JSONObject();
        req2.put("app_id", ID);
        // 57,75,100,175
        req2.put("fields", new String[] { "name", "release_date", "latest_version", "icon100", "whatsnew" });

        JSONObject req = new JSONObject();
        req.put("object", "App");
        req.put("action", "getDetails");
        req.put("args", req2);

        JSONObject rsp = _api_call(req);

        return rsp.getJSONObject("app");
    }

    public JSONObject getLinks(long ID) throws Exception {

        JSONObject req2 = new JSONObject();
        req2.put("app_id", ID);

        JSONObject req = new JSONObject();
        req.put("object", "Link");
        req.put("action", "get");
        req.put("args", req2);

        JSONObject rsp = _api_call(req);

        return rsp;
    }

    public JSONObject getUpdateInfo(Long... IDs) throws Exception {

        JSONObject req2 = new JSONObject();
        req2.put("appids", IDs);

        JSONObject req = new JSONObject();
        req.put("object", "App");
        req.put("action", "checkUpdates");
        req.put("args", req2);

        JSONObject rsp = _api_call(req);

        return rsp;
    }

    private JSONObject _api_call(JSONObject jsonData) throws Exception {

        JSONObject rq = new JSONObject();
        rq.put("request", jsonData.toString());

        NVPair params[] = { new NVPair("request", rq.toString()) };
        URL apiURL = new URL("http://api.apptrackr.org/");
        HTTPConnection con = new HTTPConnection(apiURL);
        con.setTimeout(SOCKET_TIMEOUT);
        if (m_traceOn)
            Tracer._debug("Calling API: " + jsonData);
        HTTPResponse rsp = con.Post("/", params);

        JSONObject jo = new JSONObject(rsp.getText());
        if (!jo.getString("code").equals("200")) {
            Tracer._error(APIErrorCode.getErrorInfo(jo.getString("code")));
            throw new Exception("Error in API response: " + APIErrorCode.getErrorInfo(jo.getString("code")));
        }

        jo = new JSONObject(jo.getString("data"));

        if (m_traceOn)
            Tracer._debug("API Called: " + jo);
        return jo;
    }

}
