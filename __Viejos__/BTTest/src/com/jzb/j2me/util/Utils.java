package com.jzb.j2me.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

public class Utils {
    
    public static void showException(String title, Throwable th) {
        String text = "Error [" + th.getClass() + "]: " + th.getMessage();
        Alert alert = new Alert(title, text, null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        MidletDisplay.get().setCurrent(alert);
    }
    
    public static void showMessage(String title, String text) {
        Alert alert = new Alert(title, text, null, AlertType.INFO);
        alert.setTimeout(Alert.FOREVER);
        MidletDisplay.get().setCurrent(alert);
    }

}
