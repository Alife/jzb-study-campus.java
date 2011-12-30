package com.jzb.j2me.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Image;

public class Utils {

    private static Image   s_imgInfo;
    private static boolean s_imgLoaded = false;

    public static String getExceptionMsg(Throwable th) {
        String text = "Error [" + th.getClass() + "]: " + th.getMessage();
        return text;
    }

    public static void showException(String title, Throwable th) {
        Alert alert = new Alert(title, getExceptionMsg(th), Utils.getImgInfo(), AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        MidletDisplay.setCurrent(alert);
    }

    public static void showMessage(String title, String text) {
        Alert alert = new Alert(title, text, Utils.getImgInfo(), AlertType.INFO);
        alert.setTimeout(Alert.FOREVER);
        MidletDisplay.setCurrent(alert);
    }

    public static Image getImgInfo() {
        if (!s_imgLoaded) {
            s_imgLoaded = true;
            try {
                s_imgInfo = Image.createImage("/img/info.png");
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        return s_imgInfo;
    }
}
