// AddContextMenu.java
// Gayathri Singh, April 2008, gayathri@byteblend.com

/* This class adds the registry keys needed to add a custom context menu for exe files. 
   
   The text of the custom menu and the jar file to be executed when the custom menu is selected
   can be specified using command line parameters.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.filecontextmenu.AddContextMenu [menuText] [jarPath]
*/
package jinvoke.win32demos.filecontextmenu;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.jinvoke.JInvoke;
import com.jinvoke.win32.Advapi32;
import com.jinvoke.win32.Shell32;
import static com.jinvoke.win32.WinConstants.*;

public class AddContextMenu {

	private static final String REGISTRY_JRE_KEYNAME = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
	private static final String REGISTRY_JRE_JAVAHOME_VALUENAME = "JavaHome";

	private static final int SHCNE_ASSOCCHANGED = 0x8000000;
	private static final int SHCNF_IDLIST = 0x0;

	public static void main(String[] args) {
		JInvoke.initialize();
		
		String menuText = "Check Version";
		String jarPath  = "C:\\fileinfo.jar";
		
		if (args.length == 2) {
			menuText = args[0];
			jarPath  = args[1];
		}
		addContextMenu(menuText, jarPath);
		
		System.out.println("Added context menu for \"" + menuText + "\" to run \"" + jarPath + "\".");
	}

	public static void addContextMenu(String menuText, String jarPath) {
		// specify the menu text to be shown to the user
		String keyName = "exefile\\shell\\checkversion";
		String keyValue = menuText;
		createKey(keyName, keyValue);

		// specify the command to be invoked when the custom menu is selected
		keyName = "exefile\\shell\\checkversion\\command";
		keyValue = getCommand(jarPath);
		createKey(keyName, keyValue);

		// notify the Windows shell of the change in file association information
		Shell32.SHChangeNotify(SHCNE_ASSOCCHANGED, SHCNF_IDLIST, 0, 0);
	}

	private static String getCommand(String jarPath) {
		String jrehome = getJREPath();
		String javawPath = jrehome + File.separator + "bin" + File.separator
				+ "javaw.exe";
		String command = "\"" + javawPath + "\" -jar \"" + jarPath
				+ "\" \"%1\"";
		return command;
	}

	private static String getJREPath() {
		int[] hkey = { 0 };
		if (Advapi32.RegOpenKeyEx(HKEY_LOCAL_MACHINE, REGISTRY_JRE_KEYNAME, 0,
				KEY_READ, hkey) != ERROR_SUCCESS)
			return null;
	
		int[] lpType = { 0 };
		int[] lpcbData = { MAX_PATH };
		byte[] sCurrentVersion = new byte[MAX_PATH];
	
		if (Advapi32.RegQueryValueEx(hkey[0], "CurrentVersion", null, lpType,
				sCurrentVersion, lpcbData) != ERROR_SUCCESS) {
			Advapi32.RegCloseKey(hkey[0]);
			return null;
		}
	
		String currVersion = "";
		try {
			currVersion = new String(sCurrentVersion, "UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		currVersion = currVersion.substring(0, lpcbData[0] / 2 - 1);
	
		if (Advapi32.RegOpenKeyEx(HKEY_LOCAL_MACHINE, REGISTRY_JRE_KEYNAME
				+ "\\" + currVersion, 0, KEY_READ, hkey) != ERROR_SUCCESS) {
			return null;
		}
	
		lpcbData[0] = MAX_PATH;
		byte[] sJREJavaHome = new byte[MAX_PATH];
		if (Advapi32.RegQueryValueEx(hkey[0], REGISTRY_JRE_JAVAHOME_VALUENAME,
				null, lpType, sJREJavaHome, lpcbData) != ERROR_SUCCESS) {
			Advapi32.RegCloseKey(hkey[0]);
			return null;
		}
	
		String JREHome = "";
		try {
			JREHome = new String(sJREJavaHome, "UTF-16LE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JREHome = JREHome.substring(0, lpcbData[0] / 2 - 1);
		return JREHome;
	}

	private static void createKey(String keyName, String keyValue) {
		int[] keyHandle = { 0 }; // holds created key handle from
									// RegCreateKey
	
		// If you write keys to a key under HKEY_CLASSES_ROOT, the system stores
		// the information under HKEY_LOCAL_MACHINE\Software\Classes
		Advapi32.RegCreateKeyEx(HKEY_CLASSES_ROOT, keyName, 0, null,
				REG_OPTION_NON_VOLATILE, KEY_ALL_ACCESS, null, keyHandle, null);
		// set the default value – using empty string (“”)
		Advapi32.RegSetValue(keyHandle[0], "", REG_SZ, keyValue, 0);
	}
}
