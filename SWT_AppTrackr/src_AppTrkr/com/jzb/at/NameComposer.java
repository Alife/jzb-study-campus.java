/**
 * 
 */
package com.jzb.at;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @author n63636
 * 
 */
public class NameComposer {

    public static boolean isComposedName(String composedName) {
        if (composedName.contains("N[") && composedName.contains("_PK[") && composedName.contains("_V[") && composedName.contains("_OS["))
            return true;
        else
            return false;
    }

    public static boolean isIPhoneIPA(String composedName) {
        return composedName.toLowerCase().charAt(1)=='m';
    }

    public static boolean isIPadIPA(String composedName) {
        return composedName.toLowerCase().charAt(1)=='t';
    }

    public static boolean isUniversalIPA(String composedName) {
        return composedName.toLowerCase().charAt(1)=='u';
    }

    public static boolean isLegalIPA(String composedName) {
        return _getLegal(composedName.toLowerCase())=='_';
    }

    private static char _getLegal(String composedName) {
        return composedName.charAt(0);
    }

    public static String parseName(String composedName) {
        int p1, p2;

        p1 = composedName.indexOf("N[");
        if (p1 < 0)
            return null;
        p2 = composedName.indexOf("]", p1);
        return composedName.substring(p1 + 2, p2);
    }

    public static String parsePkg(String composedName) {
        int p1, p2;

        p1 = composedName.indexOf("_PK[");
        if (p1 < 0)
            return null;
        p2 = composedName.indexOf("]", p1);
        return composedName.substring(p1 + 4, p2);
    }

    public static String parseVer(String composedName) {
        int p1, p2;

        p1 = composedName.indexOf("_V[");
        if (p1 < 0)
            return null;
        p2 = composedName.indexOf("]", p1);
        return composedName.substring(p1 + 3, p2);
    }

    private static final char VERSION_PART[] = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0' };

    public static String parseVerCanonical(String composedName) {

        StringBuffer cVer = new StringBuffer();
        String v = parseVer(composedName);
        StringTokenizer st = new StringTokenizer(v, ".");
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            StringBuffer sb = new StringBuffer();
            for (char c : t.toCharArray()) {
                if (Character.isDigit(c)) {
                    sb.append(c);
                }
            }
            sb.insert(0, VERSION_PART, 0, VERSION_PART.length - sb.length());
            cVer.append(sb);
            cVer.append('#');
        }

        return cVer.toString();
    }

    public static int compareVersion(String v1, String v2) {
        String[] tv1 = _tokenizeVersion(v1);
        String[] tv2 = _tokenizeVersion(v2);

        int max = tv1.length > tv2.length ? tv2.length : tv1.length;
        for (int n = 0; n < max; n++) {

            int i1=_parseIntSafe(tv1[n]);
            int i2=_parseIntSafe(tv2[n]);
            if (i1 != i2) {
                return (i1-i2);
            }
        }
        if (tv1.length == tv2.length) {
            return 0;
        }
        if (tv1.length > tv2.length) {
            return 1;
        } else {
            return -1;
        }
    }

    private static int _parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Throwable th) {
            return -1;
        }

    }

    private static String[] _tokenizeVersion(String v) {
        ArrayList<String> t = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(v, ".");
        while (st.hasMoreTokens()) {
            String s=st.nextToken();
            StringBuffer sb=new StringBuffer();
            for(char c:s.toCharArray()) {
                if(Character.isDigit(c))
                    sb.append(c);
            }
            t.add(sb.toString());
        }
        return t.toArray(new String[t.size()]);
    }
}
