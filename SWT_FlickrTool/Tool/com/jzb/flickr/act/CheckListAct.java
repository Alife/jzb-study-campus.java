/**
 * 
 */
package com.jzb.flickr.act;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import com.aetrion.flickr.photos.Photo;
import com.jzb.flickr.xmlbean.ITracer;

/**
 * @author n000013
 * 
 */
public class CheckListAct extends BaseAction {

    private static HashMap<String, HashMap<String, String>> s_listCache     = new HashMap<String, HashMap<String, String>>();

    private String                                          m_checkListFile = null;

    private Object                                          m_item;

    private boolean                                         m_notInList     = false;

    private String                                          m_value;

    public CheckListAct(ITracer tracer) {
        super(tracer);
    }

    /**
     * @see com.jzb.flickr.xmlbean.IAction#execute()
     */
    public Object execute() throws Exception {

        String itemStr = _strItemInfo();

        m_tracer._debug("Checking item in list: " + itemStr);

        HashMap<String, String> data = _parseCheckList();

        String value = data.get(itemStr);
        if (m_notInList) {
            if (value == null) {
                return m_item;
            } else
                return null;
        } else {
            return value;
        }

    }

    /**
     * @return the checkListFile
     */
    public String getCheckListFile() {
        return m_checkListFile;
    }

    /**
     * @return the item
     */
    public Object getItem() {
        return m_item;
    }

    /**
     * @return the notInList
     */
    public String getNotInList() {
        return m_notInList ? "true" : "false";
    }

    /**
     * @return the value
     */
    public String getValue() {
        return m_value;
    }

    /**
     * @param checkListFile
     *            the checkListFile to set
     */
    public void setCheckListFile(String inListFile) {
        m_checkListFile = inListFile;
    }

    /**
     * @param item
     *            the item to set
     */
    public void setItem(File item) {
        m_item = item;
    }

    /**
     * @param item
     *            the item to set
     */
    public void setItem(Object item) {
        m_item = item;
    }


    /**
     * @param item
     *            the item to set
     */
    public void setItem(Photo item) {
        m_item = item;
    }

    /**
     * @param notInList
     *            the notInList to set
     */
    public void setNotInList(String notInList) {
        m_notInList = Boolean.parseBoolean(notInList);
    }

    private synchronized HashMap<String, String> _parseCheckList() throws Exception {

        HashMap<String, String> data;
        data = s_listCache.get(m_checkListFile);
        if (data == null) {

            data = new HashMap<String, String>();

            BufferedReader br = new BufferedReader(new FileReader(m_checkListFile));
            while (br.ready()) {
                String key, val;
                String line = br.readLine();
                int pos = line.indexOf(',');
                if (pos > 0) {
                    key = line.substring(0, pos).trim();
                    val = line.substring(pos + 1).trim();
                } else {
                    key = line.trim();
                    val = "";
                }
                data.put(key, val);
            }
            br.close();
            
            s_listCache.put(m_checkListFile, data);
        }

        return data;
    }

    private String _strItemInfo() {

        if (m_item instanceof Photo) {
            return ((Photo) m_item).getTitle();
        } else if (m_item instanceof File) {
            // Quita la extension
            String v=((File) m_item).getName();
            int p=v.lastIndexOf('.');
            if(p>=0)
                return v.substring(0,p);
            else
                return v;
        }
        return m_item.toString();
    }
}
