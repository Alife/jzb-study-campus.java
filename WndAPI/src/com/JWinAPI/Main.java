package com.JWinAPI;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import com.jzb.wapi.KKFuti;

public class Main {

    private static JWinAPI wapi;
    private static String  strLogFN;

    /**
     * @param args
     */
    public static void main(String[] args) {

        KKFuti.kkfuti();

        wapi = new JWinAPI();

        wapi.doMessageBox("JWinAPI DLL has been loaded successfully!", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);

        int iRC = wapi.doMessageBox("Do you want to create 2 shortcuts on your desktop?\n\n1) jamesyoung1.com and, \n"
                + "2) An application shortcut to Notepad.exe? \n\nIn order to demonstrate JWINAPI's ability to create shortcuts." + "\n\nTo remove them, simply delete the shortcut.", "JWinAPI",
                MBConstants.MB_YESNO + MBConstants.MB_ICONQUESTION);

        if (iRC == MBConstants.IDYES) {
            // ////////////////////////////////////////////////////////////
            // create a desktop (URL) shortcut to www.jamesyoung1.com
            // ////////////////////////////////////////////////////////////
            try {
                wapi.doCreateInternetShortcut(wapi.doGetDesktopPath() + "JamesYoung1.URL", "http://www.jamesyoung1.com");
            } catch (Exception e) {
                wapi.doMessageBox("Exception created when creating URL shortcut!", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONERROR);
            }

            // ////////////////////////////////////////////////////////////
            // create an app shortcut to Notepad.exe on the desktop
            // ////////////////////////////////////////////////////////////
            wapi.doCreateAppShortcut("notepad.exe", SCConstants._DESKTOP, null, "My Notepad");
        }
        // /////////////////////////////////
        // MessageBox example
        // /////////////////////////////////
        doMB();

        // /////////////////////////////////
        // Save dialogue example
        // we'll get the name of our
        // 'logfile' here.
        // /////////////////////////////////
        strLogFN = doSaveDialog();
        Logger.setFilename(strLogFN);
        Logger.write("Log file opened...");

        // /////////////////////////////////
        // Get location where I will
        // save the keystrokes for
        // the inputbox function
        // /////////////////////////////////
        wapi.doMessageBox("Now, enter a location where keystrokes will be logged to (for this application only!", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);

        // /////////////////////////////////
        // Start our hook
        // /////////////////////////////////
        wapi.doStartKBHook(wapi.doSaveDialog(), false);

        // /////////////////////////////////
        // InputBox example and save the
        // keystrokes
        // /////////////////////////////////
        doInputBox();

        // /////////////////////////////////
        // End the hook
        // /////////////////////////////////
        wapi.doStopKBHook();

        // /////////////////////////////////
        // Get User Name
        // /////////////////////////////////
        doGetUserName();

        // /////////////////////////////////
        // Get Computer Name
        // /////////////////////////////////
        doGetComputerName();

        // /////////////////////////////////
        // Get Windows Directory
        // /////////////////////////////////
        doGetWindowsDirectory();

        // /////////////////////////////////
        // Get Windows Version
        // /////////////////////////////////
        doGetOSVersion();

        // /////////////////////////////////
        // Get Windows Temp Directory
        // /////////////////////////////////
        doGetCurrentPath();

        // /////////////////////////////////
        // Get Windows Temp Directory
        // /////////////////////////////////
        doGetWindowsTempDirectory();

        // /////////////////////////////////
        // Get Windows processes
        // /////////////////////////////////
        doGetWindowProcesses();

        // /////////////////////////////////
        // Get Process ID
        // /////////////////////////////////
        doGetProcessID();

        // /////////////////////////////////
        // Get the short Process name
        // /////////////////////////////////
        doGetProcessName(false);

        // /////////////////////////////////
        // Get the full Process name
        // /////////////////////////////////
        doGetProcessName(true);

        // /////////////////////////////////
        // Windows 'Open' Dialog.
        // /////////////////////////////////
        doOpenDialog();

        // /////////////////////////////////
        // Write a registry value
        // /////////////////////////////////
        doWriteToRegistry();

        // /////////////////////////////////
        // Delete a registry value
        // written by the above.. so check
        // regeedit as this runs! The
        // key that is getting deleted
        // is:
        // javaBOOLValue
        // from node:
        // Software\\jamesyoung\\regsample
        // /////////////////////////////////
        doDeleteRegistryValue();

        // /////////////////////////////////
        // Now, delete entire key
        // /////////////////////////////////
        doDeleteRegistryKey();

        // /////////////////////////////////
        // Get a Window Handle-needs title
        //
        // A window handle is crucial in
        // performing actions such as
        // sending messages, killing
        // processes and so on.
        //
        // /////////////////////////////////
        int pid = doGetWindowHandle("Google - Internet Explorer");
        if (pid > 0) {
            doKillProcess(pid);
        }

        doAlwaysOnTop("Google - Mozilla Firefox");

        pid = doGetWindowHandle("Inbox - Outlook Express");
        if (pid > 0) {
            // ////////////////////////////////////////////////////////
            // found it.. hide it from view
            // It wont even show up in task manager/applications!
            // ////////////////////////////////////////////////////////
            doShowWindow(pid, SWConstants.SW_HIDE);
        }

        // //////////////////////////////////////
        // Send a String message to a window
        // Get the message from the INPUTBOX
        // API....
        // //////////////////////////////////////
        doSendMessage(doGetWindowHandle("JWinAPIMSGRecv"), new Date() + wapi.doInputBox("JWinAPI", "Enter message to be sent to message receiver", "Type your message here!"));

        // ////////////////////////////////////////
        // Start a NOTEPAD session and send
        // keystrokes to it.. then popup
        // the Save As screen and prepopulate
        // it.
        // also flash the window just for fun.
        // ////////////////////////////////////////
        int iAppHandle = doLaunchFile("notepad.exe", SWConstants.SW_SHOWMAXIMIZED);

        // //////////////////////////////////////////////////////////
        // delay for 1 sec to get the Notepad window up...
        // //////////////////////////////////////////////////////////
        wapi.doSleep(1000l);
        wapi.doFlashWindow(doGetWindowHandle("Untitled - Notepad"), 10);
        doAppActivate("Untitled - Notepad");

        // //////////////////////////////////////////////////////////
        // send some keys... press enter.. enter text..
        //
        // choose ALT-F (File.. A(Save as) and JavaFile.txt as
        // the file name
        //
        // the example enters Hello from JWINAPI! then presses enter..
        // Then enters the text and the app handle
        // issues ALT-F Save as (%fa) % is the alt modifier. And then
        // enters MyJavaFile.txt as the file name.
        //
        // //////////////////////////////////////////////////////////////

        doSendKeys("Hello from JWINAPI!{enter}", true);

        doSendKeys("This application's handle is: " + iAppHandle, true);
        // now popup the Save As dialogue within Notepad
        doSendKeys("%faMyJavaFile.txt", true);

        wapi.doSleep(500l); // sleep some more...

        // //////////////////////////////////////////////////////
        // Launch the log file.. will open in proper app
        // //////////////////////////////////////////////////////
        doOpenLog(strLogFN, SWConstants.SW_SHOWNORMAL);

        // //////////////////////////////////////////
        // Now restore My OutLook app..
        // //////////////////////////////////////////
        if (pid > 0) {
            // Restore it
            doShowWindow(pid, SWConstants.SW_SHOWMAXIMIZED);
        }

    }

    /**
     * MessageBox example...
     */
    private static void doMB() {
        // //////////////////////////////////////////////////
        // Message box function.....
        // //////////////////////////////////////////////////

        int rc = wapi.doMessageBox("Hello World", // message text
                "JWinAPI", // Title
                MBConstants.MB_ABORTRETRYIGNORE + MBConstants.MB_ICONERROR // Button and ICON options
        );

        switch (rc) {
            case MBConstants.IDABORT: {
                wapi.doMessageBox("You clicked 'Abort'", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);
                break;
            }
            case MBConstants.IDRETRY: {
                wapi.doMessageBox("You clicked 'Retry'", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);
                break;
            }
            case MBConstants.IDIGNORE: {
                wapi.doMessageBox("You clicked 'Ignore'", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);
                break;
            }
            default:
                System.out.println("Dont really need this?");
        }
    }

    /**
     * InputBox example...
     */
    private static void doInputBox() {
        String strResponse = wapi.doInputBox("JWinAPI", "What is your name?", "Type your name here!");
        wapi.doMessageBox("Hello " + strResponse + "!", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);
        Logger.write("Hello " + strResponse + "!");
    }

    /**
     * Get Windows ProcessID
     * 
     */
    private static void doGetProcessID() {
        int iPID = wapi.doGetProcessID();
        Logger.write("This process ID is: '" + iPID + "'");
    }

    /**
     * Get the process name.
     * 
     * @param isFull --
     *            if the FULL path is to be returned
     */
    private static void doGetProcessName(boolean isFull) {
        int iPid = wapi.doGetProcessID();
        Logger.write("The process name ID is: '" + wapi.doProcessName(iPid, isFull));
    }

    /**
     * Launch the Windows Save dialogue
     * 
     */
    private static String doSaveDialog() {
        wapi.doMessageBox("You will now be prompted where to save the file\nthat will contain info from JWinAPI", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);
        return wapi.doSaveDialog();
    }

    /**
     * Launch the Windows Open dialogue
     * 
     */
    private static void doOpenDialog() {
        wapi.doMessageBox("Demo of the Windows OPEN Dialogue.  Please select a file.", "JWinAPI", MBConstants.MB_OK + MBConstants.MB_ICONASTERISK);
        Logger.write("The filename selected is: '" + wapi.doOpenDialog());
    }

    /**
     * Get handle to a window Note: This is CASE INSENSITIVE
     * 
     */
    private static int doGetWindowHandle(String strTitle) {
        int pid = wapi.doGetWindowHandle(strTitle);
        if (pid > 0) {
            Logger.write("The windows handle of '" + strTitle + "' is: '" + pid + "'");
        } else {
            wapi.doMessageBox("The window '" + strTitle + "' was not found", "JWinAPI", MBConstants.MB_ICONERROR + MBConstants.MB_ICONERROR);
            Logger.write("The window '" + strTitle + "' was not found");
        }
        return pid;
    }

    /**
     * Kill a process
     * 
     * @param pid
     */
    private static void doKillProcess(int pid) {
        wapi.doKillProcess(pid);
    }

    /**
     * open log file with its associated program.
     * 
     * @param strFN
     * @param windows
     *            options
     */
    private static void doOpenLog(String strFN, int iOpts) {
        if (wapi.doMessageBox("Do you want to open the log file?", "JWinAPI", MBConstants.MB_YESNO + MBConstants.MB_ICONQUESTION) == MBConstants.IDYES) {
            wapi.doLaunchFile(strFN, iOpts);
        }
    }

    /**
     * Launch file
     * 
     * @param strFN
     * @param windows
     *            options
     */
    private static int doLaunchFile(String strFN, int iOpts) {
        return wapi.doLaunchFile(strFN, iOpts);
    }

    /**
     * Get user name
     */
    private static void doGetUserName() {
        Logger.write("User name is: " + wapi.doGetUserName());
    }

    /**
     * Get computer name
     */
    private static void doGetComputerName() {
        Logger.write("Computer name is: " + wapi.doGetComputerName());
    }

    /**
     * Show window
     * 
     * @param iHandle
     * @param iOpts
     */
    private static void doShowWindow(int iHandle, int iOpts) {
        wapi.doShowWindow(iHandle, iOpts);
    }

    /**
     * Get windows directory
     * 
     */
    private static void doGetWindowsDirectory() {
        Logger.write("Windows Directory is: " + wapi.doGetWindowsDirectory());
    }

    /**
     * Get windows temp directory
     * 
     */
    private static void doGetWindowsTempDirectory() {
        Logger.write("Windows Temp Directory is: " + wapi.doGetWindowsTempDirectory());
    }

    /**
     * Get Windows version
     * 
     */
    private static void doGetOSVersion() {
        Logger.write("Windows Version is: " + wapi.doGetOSVersion());
    }

    /**
     * Get Windows current path
     * 
     */
    private static void doGetCurrentPath() {
        Logger.write("Current path is: " + wapi.doGetCurrentPath());
    }

    /**
     * Set or update a vlaue in the windows regtry
     * 
     */
    private static void doWriteToRegistry() {
        // write an int value
        if (wapi.doWriteToRegistry(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample", "javaINTValue", REGConstants.REG_DWORD, new Integer(123456))) {
            Logger.write("Write to registry for an int returned true");
            Logger.write("Read from registry as int: " + wapi.doReadRegistryValueAsInt(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample", "javaINTValue", REGConstants.REG_DWORD));
        } else {
            Logger.write("Write to registry for an int returned false");
        }

        // write a string value
        if (wapi.doWriteToRegistry(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample", "javaStringValue", REGConstants.REG_SZ, "MY_VALUE")) {
            Logger.write("Write to registry for a string returned true");
            Logger
                    .write("Read from registry as String: "
                            + wapi.doReadRegistryValueAsString(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample", "javaStringValue", REGConstants.REG_SZ));
        } else {
            Logger.write("Write to registry for a string returned false");
        }

        // now write a boolean value
        if (wapi.doWriteToRegistry(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample", "javaBOOLValue", REGConstants.REG_BOOLEAN, new Boolean(true))) {
            Logger.write("Write to registry for a boolean returned true");
            Logger.write("Read from registry as bool: " + wapi.doReadRegistryValueAsBool(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample", "javaBOOLValue", REGConstants.REG_BOOLEAN));
        } else {
            Logger.write("Write to registry for a boolean returned false");
        }

    }

    /**
     * Delete key from registry.
     * 
     */
    private static void doDeleteRegistryKey() {
        // delete the boolean key we set
        int iRC = wapi.doDeleteRegistryKey(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample");
        if (iRC != 0) {
            Logger.write("Delete from registry for a boolean returned true");
        } else {
            Logger.write("Delete from registry for a boolean returned false");
        }
    }

    /**
     * Delete value from registry. delete javaBOOLValue -- set above.
     */
    private static void doDeleteRegistryValue() {
        // delete the boolean value we set
        int iRC = wapi.doDeleteRegistryValue(REGConstants.HKEY_CURRENT_USER, "Software\\jamesyoung\\regsample", "javaBOOLValue");
        if (iRC == 0) {
            Logger.write("Delete from registry for a value worked!");
        } else {
            Logger.write("Delete from registry for a value didn't work.  Return code of: " + iRC);
        }
    }

    /**
     * Get windows processes and relevent info..
     * 
     */
    private static void doGetWindowProcesses() {
        JWinProcess[] jwpa = wapi.doGetWindowProcesses();
        for (int i = 0; i < jwpa.length; i++) {
            if (jwpa[i].hasTitle()) {
                Logger.write("Window title: " + jwpa[i].getWTitle() + ",PID: " + jwpa[i].getPID() + ",Executable name: " + jwpa[i].getExecName() + ",Is Visible? " + jwpa[i].isVisible());
            }
        }
    }

    /**
     * Send a string message using WM_COPYDATA to another window.
     * 
     * @param hw
     * @param str
     */
    private static void doSendMessage(int hw, String str) {
        wapi.doSendMessage(hw, str);
    }

    /**
     * Activate application
     * 
     * @param title --
     *            Window title
     */
    private static void doAppActivate(String title) {
        wapi.doAppActivate(title);
    }

    /**
     * Send keystrokes
     * 
     * @param keys
     * @param wait
     */
    private static void doSendKeys(String keys, boolean wait) {
        wapi.doSendKeys(keys, wait);
    }

    /**
     * Set a window to be always on top.
     * 
     * @param s
     */
    private static void doAlwaysOnTop(String s) {
        int wHandle = wapi.doGetWindowHandle(s);
        if (wHandle > 0) {
            // set it to always on top
            wapi.doAlwaysOnTop(wHandle);
        }

    }


}

/**
 * Helper class to write info...
 * 
 * @author YOUNGJ
 * 
 */
class Logger {

    private static String strFilename;

    public static void write(String s) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(strFilename, true));
            out.write(new Date() + "-" + s + "\n");
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("Cannot open log file because of: " + e.toString());
        }
    }

    /**
     * Set location of log file
     * 
     * @param s
     */
    public static void setFilename(String s) {
        strFilename = s;
    }
}