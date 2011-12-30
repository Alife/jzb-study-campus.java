/**
 * 
 */
package com.isb.patch.aix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This utility class has been created to replace properties without losing the comments and the relative position of all elements
 * 
 * This code has been copied from the actual Properties Java class.
 * 
 * @author PS00A501
 * 
 */
public class CommentedProperties {

    private static final char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private ArrayList         m_lines    = new ArrayList();

    private HashMap           m_map      = new HashMap();

    private static char toHex(int i) {
        return hexDigit[i & 15];
    }

    private static void writeln(BufferedWriter bufferedwriter, String s) throws IOException {
        bufferedwriter.write(s);
        bufferedwriter.newLine();
    }

    /**
     * Default constructor
     */
    public CommentedProperties() {
    }

    public void load(InputStream is) throws IOException {

        m_lines.clear();
        m_map.clear();
        
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(is, "8859_1"));
        do {
            String s;
            int j;
            int k;
            do {
                char c;
                boolean flag;
                do {
                    do {
                        s = bufferedreader.readLine();
                        if (s == null)
                            return;
                    } while (s.length() <= 0);
                    c = s.charAt(0);
                    int i = 0;
                    flag = false;
                    for (; c == ' ' || c == '\t'; c = s.charAt(i)) {
                        if (++i != s.length())
                            continue;
                        flag = true;
                        break;
                    }

                    if (c == '#' || c == '!') {
                        m_lines.add("#" + s.substring(i + 1));
                    }

                } while (flag || c == '#' || c == '!');
                String s1;
                String s2;
                for (; continueLine(s); s = new String(s2 + s1)) {
                    s1 = bufferedreader.readLine();
                    if (s1 == null)
                        s1 = new String("");
                    s2 = s.substring(0, s.length() - 1);
                    int l = 0;
                    for (l = 0; l < s1.length(); l++)
                        if (" \t\r\n\f".indexOf(s1.charAt(l)) == -1)
                            break;

                    s1 = s1.substring(l, s1.length());
                }

                j = s.length();
                for (k = 0; k < j; k++)
                    if (" \t\r\n\f".indexOf(s.charAt(k)) == -1)
                        break;

            } while (k == j);
            int i1;
            for (i1 = k; i1 < j; i1++) {
                char c1 = s.charAt(i1);
                if (c1 == '\\') {
                    i1++;
                    continue;
                }
                if ("=: \t\r\n\f".indexOf(c1) != -1)
                    break;
            }

            int j1;
            for (j1 = i1; j1 < j; j1++)
                if (" \t\r\n\f".indexOf(s.charAt(j1)) == -1)
                    break;

            if (j1 < j && "=:".indexOf(s.charAt(j1)) != -1)
                j1++;
            for (; j1 < j; j1++)
                if (" \t\r\n\f".indexOf(s.charAt(j1)) == -1)
                    break;

            String s3 = s.substring(k, i1);
            String s4 = i1 >= j ? "" : s.substring(j1, j);
            s3 = loadConvert(s3);
            s4 = loadConvert(s4);

            m_lines.add(s3);
            m_map.put(s3, s4);

        } while (true);
    }

    public void store(OutputStream outputstream, String s) throws IOException {

        BufferedWriter bufferedwriter = new BufferedWriter(new OutputStreamWriter(outputstream, "8859_1"));
        if (s != null)
            writeln(bufferedwriter, "#" + s);
        writeln(bufferedwriter, "# Stored - " + (new Date()).toString());
        String s1;
        String s2;
        Iterator iter = m_lines.iterator();
        while (iter.hasNext()) {
            s1 = (String) iter.next();
            if (s1.startsWith("#")) {
                writeln(bufferedwriter, s1);
            } else {
                s2 = (String) m_map.get(s1);
                s1 = saveConvert(s1, true);
                s2 = saveConvert(s2, false);
                writeln(bufferedwriter, s1 + "=" + s2);
            }
        }

        bufferedwriter.flush();
    }

    private boolean continueLine(String s) {
        int i = 0;
        for (int j = s.length() - 1; j >= 0 && s.charAt(j--) == '\\';)
            i++;

        return i % 2 == 1;
    }

    private String loadConvert(String s) {
        int i = s.length();
        StringBuffer stringbuffer = new StringBuffer(i);
        for (int j = 0; j < i;) {
            char c = s.charAt(j++);
            if (c == '\\') {
                c = s.charAt(j++);
                if (c == 'u') {
                    int k = 0;
                    for (int l = 0; l < 4; l++) {
                        c = s.charAt(j++);
                        switch (c) {
                            case 48: // '0'
                            case 49: // '1'
                            case 50: // '2'
                            case 51: // '3'
                            case 52: // '4'
                            case 53: // '5'
                            case 54: // '6'
                            case 55: // '7'
                            case 56: // '8'
                            case 57: // '9'
                                k = ((k << 4) + c) - 48;
                                break;

                            case 97: // 'a'
                            case 98: // 'b'
                            case 99: // 'c'
                            case 100: // 'd'
                            case 101: // 'e'
                            case 102: // 'f'
                                k = ((k << 4) + 10 + c) - 97;
                                break;

                            case 65: // 'A'
                            case 66: // 'B'
                            case 67: // 'C'
                            case 68: // 'D'
                            case 69: // 'E'
                            case 70: // 'F'
                                k = ((k << 4) + 10 + c) - 65;
                                break;

                            case 58: // ':'
                            case 59: // ';'
                            case 60: // '<'
                            case 61: // '='
                            case 62: // '>'
                            case 63: // '?'
                            case 64: // '@'
                            case 71: // 'G'
                            case 72: // 'H'
                            case 73: // 'I'
                            case 74: // 'J'
                            case 75: // 'K'
                            case 76: // 'L'
                            case 77: // 'M'
                            case 78: // 'N'
                            case 79: // 'O'
                            case 80: // 'P'
                            case 81: // 'Q'
                            case 82: // 'R'
                            case 83: // 'S'
                            case 84: // 'T'
                            case 85: // 'U'
                            case 86: // 'V'
                            case 87: // 'W'
                            case 88: // 'X'
                            case 89: // 'Y'
                            case 90: // 'Z'
                            case 91: // '['
                            case 92: // '\\'
                            case 93: // ']'
                            case 94: // '^'
                            case 95: // '_'
                            case 96: // '`'
                            default:
                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }

                    stringbuffer.append((char) k);
                } else if (c == 't')
                    stringbuffer.append('\t');
                else if (c == 'r')
                    stringbuffer.append('\r');
                else if (c == 'n')
                    stringbuffer.append('\n');
                else if (c == 'f')
                    stringbuffer.append('\f');
                else
                    stringbuffer.append(c);
            } else {
                stringbuffer.append(c);
            }
        }

        return stringbuffer.toString();
    }

    private String saveConvert(String s, boolean flag) {
        int i = s.length();
        StringBuffer stringbuffer = new StringBuffer(i * 2);
        for (int j = 0; j < i; j++) {
            char c = s.charAt(j);
            switch (c) {
                case 32: // ' '
                    if (j == 0 || flag)
                        stringbuffer.append('\\');
                    stringbuffer.append(' ');
                    break;

                case 92: // '\\'
                    stringbuffer.append('\\');
                    stringbuffer.append('\\');
                    break;

                case 9: // '\t'
                    stringbuffer.append('\\');
                    stringbuffer.append('t');
                    break;

                case 10: // '\n'
                    stringbuffer.append('\\');
                    stringbuffer.append('n');
                    break;

                case 13: // '\r'
                    stringbuffer.append('\\');
                    stringbuffer.append('r');
                    break;

                case 12: // '\f'
                    stringbuffer.append('\\');
                    stringbuffer.append('f');
                    break;

                default:
                    if (c < ' ' || c > '~') {
                        stringbuffer.append('\\');
                        stringbuffer.append('u');
                        stringbuffer.append(toHex(c >> 12 & 15));
                        stringbuffer.append(toHex(c >> 8 & 15));
                        stringbuffer.append(toHex(c >> 4 & 15));
                        stringbuffer.append(toHex(c & 15));
                        break;
                    }
                    if ("= \t\r\n\f#!".indexOf(c) != -1)
                        stringbuffer.append('\\');
                    stringbuffer.append(c);
                    break;
            }
        }

        return stringbuffer.toString();
    }

    public String getProperty(String key) {
        String val = (String) m_map.get(key);
        return val;
    }

    public String setProperty(String key, String val) {
        // add, if is a new key, to the properties lines
        if(!m_map.containsKey(key)) {
            m_lines.add(key);
        }
        String previousVal = (String) m_map.put(key, val);
        return previousVal;
    }
    
    public void clear() {
        m_map.clear();
        m_lines.clear();
    }
}
