package com.jzb.blt.txt;

import com.jzb.blt.StoreManager;

public class TextManager {

    public static TextData loadTextData(String alias, int index) throws Exception {

        byte buffer[] = StoreManager.loadFile(index);
        char data[] = StoreManager.byteToChar(buffer);
        return new TextData(alias, data, data.length);
    }
}
