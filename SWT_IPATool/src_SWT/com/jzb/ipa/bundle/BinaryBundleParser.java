/**
 * 
 */
package com.jzb.ipa.bundle;

import java.util.HashMap;

/**
 * @author n000013
 * 
 */
public class BinaryBundleParser implements IBundleDataParser {

    public BundleData parse(byte buffer[]) throws Exception {

        BundleData bdata = new BundleData();

        HashMap<String, String> binData = _parse(buffer);

        bdata.name = _getValue(binData, "CFBundleDisplayName");
        if (bdata.name == null || bdata.name.equals(""))
            bdata.name = _getValue(binData, "CFBundleExecutable");
        bdata.version = _getValue(binData, "CFBundleVersion");
        bdata.minOSVersion = _getValue(binData, "MinimumOSVersion");
        bdata.pkgID = _getValue(binData, "CFBundleIdentifier");

        return bdata;

    }

    private String _getValue(HashMap<String, String> binData, String key) {
        String v = binData.get(key);
        if (v == null)
            v = "";
        return v;
    }

    public HashMap<String, String> _parse(byte buffer[]) throws Exception {

        HashMap<String, String> binData = new HashMap<String, String>();

        // Lee el array de indices
        int p1 = _readInt(buffer, buffer.length - 2);
        int offsets[] = new int[(buffer.length - p1) / 2];
        for (int n = 0; n < offsets.length; n++) {
            offsets[n] = _readInt(buffer, p1);
            p1 += 2;
        }

        // Lee la informacion de cada elemento atendiendo al array de indices
        int numElem = 0;
        int firstElem = 0;
        int typeFmt = 0x00ff & buffer[offsets[0]];
        switch (typeFmt) {
            case 0xDF:
                numElem = (offsets[1] - offsets[0] - 3) / 2;
                firstElem = 3;
                break;
            case 0xDC:
            case 0xDE:
            case 0xDD:
                numElem = (offsets[1] - offsets[0] - 1) / 2;
                firstElem = 1;
                break;
            default:
                throw new Exception("Unknown binary plist format: 0x" + Integer.toHexString(typeFmt).toUpperCase());
        }

        for (int n = 0; n < numElem; n++) {
            int elemIndex, index;

            elemIndex = 0x00FF & buffer[offsets[0] + firstElem + n];
            index = offsets[elemIndex];
            String name = _readData(buffer, index);

            elemIndex = 0x00FF & buffer[numElem + offsets[0] + firstElem + n];
            index = offsets[elemIndex];
            String value = _readData(buffer, index);

            binData.put(name, value);
        }

        return binData;
    }

    private String _readData(byte buffer[], int pos) {

        StringBuffer sb = new StringBuffer();

        int type = 0x00FF & buffer[pos++];
        int len;

        switch (type) {
            case 95:
                pos++; // ??
                len = buffer[pos++];
                for (int n = 0; n < len; n++) {
                    sb.append((char) buffer[pos++]);
                }
                break;

            case 161:
                pos++; // ??
                len = (byte) (buffer[pos++] - 0x50);
                for (int n = 0; n < len; n++) {
                    sb.append((char) buffer[pos++]);
                }
                break;

            case 9:
                sb.append("true");
                break;

            default:
                if (buffer[pos] != 0) {
                    len = (byte) (type - 0x50);
                    for (int n = 0; n < len; n++) {
                        sb.append((char) buffer[pos++]);
                    }
                } else {
                    len = (byte) (type - 0x55) / 2;
                    for (int n = 0; n < len; n++) {
                        int b1 = 0x00ff & (int) buffer[pos++];
                        int b2 = 0x00ff & (int) buffer[pos++];
                        int i = b1 * 256 + b2;
                        sb.append((char) i);
                    }
                }
                break;
        }

        return sb.toString();
    }

    private int _readInt(byte buffer[], int pos) {
        int x = (0x00FF & buffer[pos + 0]) << 8;
        int y = (0x00FF & buffer[pos + 1]);
        int z = x + y;
        return z;
    }

}
