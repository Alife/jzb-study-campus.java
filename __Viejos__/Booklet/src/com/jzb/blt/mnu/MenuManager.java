package com.jzb.blt.mnu;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.jzb.blt.StoreManager;

public class MenuManager {

    public static MenuItem loadTreeModel() throws Exception {

        MenuItem root = MenuItem.createRoot();

        byte bufferMenu[] = StoreManager.loadMenu();
        if (bufferMenu == null)
            return root;

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bufferMenu));
        root.readExternal(dis);
        dis.close();

        return root;
    }

}
