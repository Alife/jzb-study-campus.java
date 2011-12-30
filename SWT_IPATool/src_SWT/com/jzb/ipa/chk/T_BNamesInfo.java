/**
 * 
 */
package com.jzb.ipa.chk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

public class T_BNamesInfo {

    // fileName
    private ArrayList<String>       m_toIgnore  = new ArrayList<String>();
    // pkg, fileName
    private HashMap<String, String> m_toProcess = new HashMap<String, String>();

    public T_BNamesInfo() {
    }

    public T_BNamesInfo(T_BNamesInfo tbni) {
        m_toProcess.putAll(tbni.m_toProcess);
        m_toIgnore.addAll(tbni.m_toIgnore);
    }

    public void addAll(T_BNamesInfo tni) {

        for (String oldBName : tni.m_toProcess.values()) {
            addBundleName(oldBName);
        }
        m_toIgnore.addAll(tni.m_toIgnore);

    }

    public void addBundleName(String bdName) {

        String pkg = _getBNameElement(bdName, "PK");
        // doesn't follow the bundle name's pattern
        if (pkg == null) {
            return;
        }

        String prevBundle = m_toProcess.get(pkg);
        if (prevBundle == null) {
            m_toProcess.put(pkg, bdName);
        } else {
            String vnew = _fmtVersionToComp(_getBNameElement(bdName, "V"));
            String vprv = _fmtVersionToComp(_getBNameElement(prevBundle, "V"));
            int c = vnew.compareTo(vprv);
            if (c > 0) {
                m_toProcess.put(pkg, bdName);
                m_toIgnore.add(prevBundle);
            } else {
                if (c != 0)
                    m_toIgnore.add(bdName);
            }
        }

    }

    public void clearToBeIgnored() {
        m_toIgnore.clear();
    }

    public ArrayList<String> getEquivalents(Collection<String> bndList) {

        ArrayList<String> eqvList = new ArrayList<String>();

        for (String bdName : bndList) {

            String pkg = _getBNameElement(bdName, "PK");

            // doesn't follow the bundle name's pattern
            if (pkg == null) {
                continue;
            }

            String newName = m_toProcess.get(pkg);
            if (newName != null)
                eqvList.add(newName);
        }

        return eqvList;
    }

    public Collection<String> getToIgnoreBNames() {
        return m_toIgnore;
    }

    public Collection<String> getToProcessBNames() {
        return m_toProcess.values();
    }

    public void removeAll(T_BNamesInfo tni) {

        for (String toRemoveBName : tni.m_toProcess.values()) {

            String pkg = _getBNameElement(toRemoveBName, "PK");
            String existingBName = m_toProcess.get(pkg);
            if (existingBName != null) {
                String vexi = _fmtVersionToComp(_getBNameElement(existingBName, "V"));
                String vrem = _fmtVersionToComp(_getBNameElement(toRemoveBName, "V"));
                if (vexi.compareTo(vrem) <= 0) {
                    m_toProcess.remove(pkg);
                    m_toIgnore.add(existingBName);
                }
            }

        }
    }

    private String _filterNumeric(String str) {

        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < str.length(); n++) {
            char c = str.charAt(n);
            if (Character.isDigit(c) || c == '.') {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private String _fmtVersionToComp(String version) {

        final int MAX_VERSION_ELEMENTS = 5; // number of elements dot separated

        int index = MAX_VERSION_ELEMENTS;
        StringBuffer numVers = new StringBuffer();

        if (version != null) {
            StringTokenizer st = new StringTokenizer(_filterNumeric(version), ".");
            while (st.hasMoreTokens()) {
                String v = st.nextToken();
                numVers.append(_padLeftStr(v, MAX_VERSION_ELEMENTS, '0'));
                index--;
                if (index < 0) {
                    throw new RuntimeException("Only version numbers with " + MAX_VERSION_ELEMENTS + " elements are admited");
                }
            }
        }

        String rpadding = _padLeftStr("", MAX_VERSION_ELEMENTS, '0');
        for (int n = 0; n < index; n++) {
            numVers.append(rpadding);
        }

        return numVers.toString();
    }

    private String _getBNameElement(String name, String part) {

        String startStr = "_" + part + "[";
        int p1 = name.indexOf(startStr);
        if (p1 < 0)
            return null;

        int p2 = name.indexOf("]", p1);
        if (p2 < 0)
            return null;

        return name.substring(p1 + startStr.length(), p2);
    }

    private String _padLeftStr(String s, int size, char c) {
        StringBuffer sb = new StringBuffer();
        for (int n = 0; n < size - s.length(); n++) {
            sb.append(c);
        }
        sb.append(s);
        return sb.toString();
    }
}