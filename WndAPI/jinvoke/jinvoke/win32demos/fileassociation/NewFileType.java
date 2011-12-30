// NewFileType.java
// Gayathri Singh, April 2008, gayathri@byteblend.com

/* This class adds the registry keys needed to register a new file type.
   
   The NewFileTypeUI class provides a GUI frontend for this class, though this class can be used 
   independent of the user interface. It provides the registerNewFileType() method that can be 
   called passing in the information needed to register the new file type. The method computes
   the registry keys that need to be added, and adds them using the Registry APIs from Advapi32.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.fileassociation.NewFileTypeUI
*/

package jinvoke.win32demos.fileassociation;

import java.io.File;
import java.io.UnsupportedEncodingException;

import com.jinvoke.JInvoke;
import com.jinvoke.win32.Advapi32;
import com.jinvoke.win32.Shell32;
import static com.jinvoke.win32.WinConstants.*;

public class NewFileType {
	static {
		JInvoke.initialize();
	}

	private static final String REGISTRY_JRE_KEYNAME = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
	private static final String REGISTRY_JRE_JAVAHOME_VALUENAME = "JavaHome";

	private static final int SHCNE_ASSOCCHANGED = 0x8000000;
	private static final int SHCNF_IDLIST = 0x0;

	public void registerNewFileType(String fileExtension, String progID,
			String fileDescription, String iconPath, String jarPath) {

		// create HKEY_LOCAL_MACHINE\.foo with progID as default value
		String keyName = "." + fileExtension;
		String keyValue = progID;
		createKey(keyName, keyValue);

		// create HKEY_LOCAL_MACHINE\progID with file description as default
		// value
		keyName = progID;
		keyValue = fileDescription;
		createKey(keyName, keyValue);

		// create HKEY_LOCAL_MACHINE\progID\DefaultIcon with path to icon as
		// value
		keyName = progID + "\\DefaultIcon";
		keyValue = iconPath;
		createKey(keyName, keyValue);

		// associate the executable jar at jarPath with this file type
		keyName = progID + "\\Shell\\Open\\Command";
		keyValue = getCommand(jarPath);
		createKey(keyName, keyValue);
		
		// notify the Windows shell of the new file type
		Shell32.SHChangeNotify(SHCNE_ASSOCCHANGED, SHCNF_IDLIST, 0, 0);
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

	private static String getCommand(String jarPath) {
		String jrehome = getJREPath();
		String javawPath = jrehome + File.separator + "bin" + File.separator
				+ "javaw.exe";
		String command = "\"" + javawPath + "\" -jar \"" + jarPath
				+ "\" \"%1\"";
		return command;
	}

	private void createKey(String keyName, String keyValue) {
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
