/**
 * 
 */
package com.jzb.ipa.ren;

import java.util.StringTokenizer;

import com.jzb.ipa.bundle.T_BundleData;
import com.jzb.ipa.plist.T_PLArray;
import com.jzb.util.Tracer;

/**
 * @author n63636
 * 
 */
public class NameComposer {

    public static boolean isComposedName(String composedName) {

        composedName = composedName.toLowerCase();
        
        char cLegal = _getLegal(composedName);
        char cType = _getType(composedName);

        if ((cType == 'm' || cType == 't' || cType == 'u') && 
            (cLegal == '_' || cLegal == '$' || cLegal == '#') && 
             composedName.contains("_pk[") && 
             composedName.contains("_v[") && 
             composedName.contains("_os["))
            return true;
        else
            return false;
    }

    private static char _getLegal(String composedName) {
        return composedName.charAt(0);
    }
    
    private static char _getType(String composedName) {
        return composedName.charAt(1);
    }
    
    public static boolean isIPhoneIPA(String composedName) {
        return _getType(composedName.toLowerCase())=='m';
    }

    public static boolean isIPadIPA(String composedName) {
        return _getType(composedName.toLowerCase())=='t';
    }

    public static boolean isUniversalIPA(String composedName) {
        return _getType(composedName.toLowerCase())=='u';
    }

    public static boolean isLegalIPA(String composedName) {
        return _getLegal(composedName.toLowerCase())=='_';
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

    public static String composeName(T_BundleData ipaInfo) {

        StringBuffer sb = new StringBuffer();

        String CFBundleName = ipaInfo.dict.getStrValue("CFBundleName");
        String CFBundleDisplayName = ipaInfo.dict.getStrValue("CFBundleDisplayName");
        if(CFBundleName==null && CFBundleDisplayName==null) {
            CFBundleName=ipaInfo.dict.getStrValue("CFBundleIdentifier");
        }
        
        String name = _mergeNames(CFBundleName, CFBundleDisplayName);
        name=name.replace(':','_');
        String pkg = ipaInfo.dict.getStrValue("CFBundleIdentifier");
        String ver = ipaInfo.dict.getStrValue("CFBundleVersion");
        String os = ipaInfo.dict.getStrValue("MinimumOSVersion");

        sb.append(ipaInfo.isLegal);
        sb.append(_calcAppType(ipaInfo));
        sb.append("N[");
        sb.append(name);
        sb.append("]_PK[");
        sb.append(pkg);
        sb.append("]_V[");
        sb.append(ver);
        sb.append("]_OS[");
        sb.append(os);
        sb.append("]_D[");
        sb.append(ipaInfo.fdate);
        sb.append("]");

        return sb.toString();
    }

    private static String _calcAppType(T_BundleData ipaInfo) {

        T_PLArray disps = (T_PLArray) ipaInfo.dict.getValue("UIDeviceFamily");
        if (disps == null) {
            return "m";
        } else {
            if (disps.getSize() > 1) {
                return "u";
            } else {
                int type;

                Object obj = disps.getValue(0);
                if (obj instanceof Number) {
                    type = ((Number) obj).intValue();
                } else if (obj instanceof String) {
                    type = Integer.parseInt((String) obj);
                } else {
                    Tracer._error("Unknown device type class: " + obj.getClass());
                    type = -1;
                }

                switch (type) {
                    case 1:
                        return "m";
                    case 2:
                        return "t";
                    default:
                        Tracer._error("Unknown device type: " + type);
                        return "x";
                }
            }
        }
    }

    private static String _mergeNames(String s1, String s2) {

        if (s1 == null)
            return s2;
        if (s2 == null)
            return s1;

        StringBuffer sb1 = new StringBuffer();
        sb1.append(s1.charAt(0));
        for (int n = 1; n < s1.length(); n++) {
            char c = s1.charAt(n);
            if (Character.isUpperCase(c) || Character.isWhitespace(c) || Character.isDigit(c) || !Character.isLetter(c))
                break;
            sb1.append(c);
        }

        StringBuffer sb2 = new StringBuffer();
        sb2.append(s2.charAt(0));
        for (int n = 1; n < s2.length(); n++) {
            char c = s2.charAt(n);
            if (Character.isUpperCase(c) || Character.isWhitespace(c) || Character.isDigit(c) || !Character.isLetter(c))
                break;
            sb2.append(c);
        }

        if (sb1.length() > sb2.length())
            sb1.setLength(sb2.length());
        if (sb2.length() > sb1.length())
            sb2.setLength(sb1.length());

        String compName;
        if (sb1.toString().toLowerCase().equals(sb2.toString().toLowerCase())) {
            if (s1.length() > s2.length())
                compName = s1;
            else
                compName = s2;
        } else {
            compName = s1 + "-" + s2;
        }

        char invChars[] = { '<', '>' };
        for (char c : invChars) {
            compName = compName.replace(c, '-');
        }

        return compName;

    }

}
