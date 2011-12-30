/**
 * 
 */
package com.jzb.pdp;

import java.io.*;

class PWIEmbeddedWAVExport {

    public static void main(String[] s) throws Exception {
        
        s=new String[] {"D:\\JZarzuela\\D_S_Escritorio\\Nota2.pwi"};
        
        FileInputStream fis = new FileInputStream(s[0]);
        File f = new File(s[0]);
        int fileSize = (int) f.length();
        byte[] barr = new byte[fileSize];
        byte[] barr2 = new byte[fileSize / 2];
        int readCount = fis.read(barr);
        for (int i = 0; i < fileSize / 2; i++)
            barr2[i] = barr[i * 2];
        String str = new String(barr2);
        java.util.Vector indeces = new java.util.Vector();
        int lastIndexFound = 0;
        while ((lastIndexFound = str.indexOf("VoiceNote.wav", lastIndexFound + 1)) != -1)
            indeces.addElement(new Integer(lastIndexFound * 2));
        RandomAccessFile raf = new RandomAccessFile(s[0], "r");
        // now, we have the exact position of all VoiceNote.wav's in the file;
        // we can go on for the actual extraction
        int sumSize = 0;
        for (int i = 0; i < indeces.size(); i++) {
            int embFileStartsAt = ((Integer) indeces.elementAt(i)).intValue() + "VoiceNote.wav".length() * 2 - 2 + 4146 + 4;
            int embFileEndsAt = readCount;
            if (i < indeces.size() - 1)
                embFileEndsAt = ((Integer) indeces.elementAt(i + 1)).intValue() - 528;
            FileOutputStream fos = new FileOutputStream(s[0].substring(0, s[0].length() - 4) + (indeces.size() - i) + ".wav");
            // indeces.size()-i is needed because of the LIFO order in the file
            fos.write(barr, embFileStartsAt, embFileEndsAt - embFileStartsAt);
            fos.flush();
            fos.close();
        }
        fis.close();
    } // main
}// class
