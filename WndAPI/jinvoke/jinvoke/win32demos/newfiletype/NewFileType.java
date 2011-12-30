// NewFileType.java
// Gayathri Singh, April 2008, gayathri@byteblend.com

/* This class adds the registry keys needed to register a new file type. 
   
   The NewFileTypeUI class provides a GUI frontend for this class, though this class can be used 
   independent of the user interface. It provides the registerNewFileType() method that can be 
   called passing in the information needed to register the new file type. The method computes
   the registry keys that need to be added, and adds them using the Registry APIs from Advapi32.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.newfiletype.NewFileTypeUI
*/

package jinvoke.win32demos.newfiletype;

import com.jinvoke.JInvoke;
import com.jinvoke.win32.Advapi32;
import com.jinvoke.win32.Shell32;

import static com.jinvoke.win32.WinConstants.*;

public class NewFileType {
	public static final int SHCNE_ASSOCCHANGED = 0x8000000;
	public static final int SHCNF_IDLIST = 0x0;

	static {
		JInvoke.initialize();
	}

	public void registerNewFileType(String fileExtension, 
									String progID,
									String fileDescription, 
									String iconPath) {
		
		// create HKEY_LOCAL_MACHINE\.foo with progID as default value
		String keyName = "." + fileExtension;
		String keyValue = progID;
		createKey(keyName, keyValue);
	
		// create HKEY_LOCAL_MACHINE\progID with file description as default value
		keyName = progID;
		keyValue = fileDescription;
		createKey(keyName, keyValue);
	
		// create HKEY_LOCAL_MACHINE\progID\DefaultIcon with path to icon as value
		keyName = progID + "\\DefaultIcon";
		keyValue = iconPath;
		createKey(keyName, keyValue);
		
		// notify the Windows shell of the new file type
		Shell32.SHChangeNotify(SHCNE_ASSOCCHANGED, SHCNF_IDLIST, 0, 0);
	}

	private void createKey(String keyName, String keyValue) {
		int[] keyHandle = { 0 }; // holds created key handle from RegCreateKey
		
		// If you write keys to a key under HKEY_CLASSES_ROOT, the system stores 
		// the information under HKEY_LOCAL_MACHINE\Software\Classes
		Advapi32.RegCreateKeyEx(HKEY_CLASSES_ROOT, keyName, 0, null, 
				                REG_OPTION_NON_VOLATILE, KEY_ALL_ACCESS, null, keyHandle, null);
		// set the default value – using empty string (“”)
		Advapi32.RegSetValue(keyHandle[0], "", REG_SZ, keyValue, 0);
	}

}
