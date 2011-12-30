/**
 * 
 */
package com.jzb.at;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author n63636
 * 
 */
public class IPAData {

    JSONObject appInfo = null;
    JSONArray linksInfo = null;
    String     bundle;
    boolean    forIPad;
    long       id      = -1;
    String     name = null;
    String     newVersion = null;
    boolean    updated = false;
    String     version;
    String     price;
    boolean    legal;

    public IPAData() {
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        try {
            JSONObject o = new JSONObject();
            o.put("name", name);
            o.put("bundle", bundle);
            o.put("id", id);
            o.put("version", version);
            o.put("newVersion", newVersion);
            o.put("updated", updated);
            o.put("price", price);
            return o.toString();
        } catch (JSONException ex) {
            return "error in toString";
        }
    }

}
