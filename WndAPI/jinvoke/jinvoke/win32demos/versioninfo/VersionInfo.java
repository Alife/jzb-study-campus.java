// VersionInfo.java
// Gayathri Singh, March 2008, gayathri@byteblend.com

/* This class encapsulates version information for a specified dll or exe.
   The file information is obtained using the Version APIs exported from Version32.dll
   
   An instance of this class is created by constructing a FileVersionInfo object, passing in 
   the path to the dll or exe whose version information is to be obtained. 
   The version information can then be queried using the public fields of this class.
   
   This class is designed to be used independently, and can be run as a console application.
   The FileVersionUI class provides a GUI frontend for this class.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.versioninfo.VersionInfo  [ file-path ]
*/
package jinvoke.win32demos.versioninfo;

import static com.jinvoke.win32.WinConstants.*;

import com.jinvoke.Util;
import com.jinvoke.win32.Version;
import com.jinvoke.win32.structs.VS_FixedFileInfo;

public class VersionInfo {
    
	// fixed version information
    public int versionMajor;
    public int versionMinor;
    public int versionBuild;
    public int versionPrivatePart;
    public String fileVersionString = "";
    public String fileAttributes = "";
    public String fileOS = "";
    public String fileType = "";
    public String fileSubType = "";
    public String language = "";
    
    // language dependent predefined version information strings
    public String comments = "";
    public String internalName = "";
    public String productName = "";
    public String companyName = "";
    public String legalCopyright = "";
    public String productVersion = "";
    public String fileDescription = "";
    public String legalTrademarks = "";
    public String privateBuild = "";
    public String fileVersion = "";
    public String originalFilename = "";
    public String specialBuild = "";
    
    // sub-blocks used by VerQueryValue function
    private final String ROOT_BLOCK = "\\";
    private final String TRANSLATION_BLOCK = "\\VarFileInfo\\Translation";
    
    // constructor
    public VersionInfo(String filename) {
    	int[] handle = { 0 };
    	
    	// check if version info is available for this file
    	// if it is, versionInfoSize will return the size of that information
        int versionInfoSize = Version.GetFileVersionInfoSize(filename, handle);
        if (versionInfoSize > 0) {
        	
        	// version info is available
        	// get the version info in a buffer of versionInfoSize bytes
        	byte [] data = new byte[versionInfoSize];
        	boolean succeeded = Version.GetFileVersionInfo(filename, handle[0], versionInfoSize, data);
        	if (succeeded) {
        		int[] bytesReturned = { 0 };
        		
        		// Get the fixed (language and codepage independent) version information
        		int[] ptr = { 0 };
        		Version.VerQueryValue(data, ROOT_BLOCK, ptr, bytesReturned);
                VS_FixedFileInfo fixedFileInfo =  Util.ptrToStruct(ptr[0], VS_FixedFileInfo.class);
               
                // Set the fixed file version information for this file 
                setFixedFileInfo(fixedFileInfo);
                
                // Get the translation block - this provides the language and codepage identifiers
                // These are used to obtain the language-specific predefined strings later
                Version.VerQueryValue(data, TRANSLATION_BLOCK, ptr, bytesReturned);
                if (bytesReturned[0] > 0) {
                	Translation translation =  Util.ptrToStruct(ptr[0], Translation.class);
                    
                	// convert the language and codepage identifiers to hexadecimal strings
                	String codepageID = String.format("%1$04x", translation.codepageID);
                    String languageID = String.format("%1$04x", translation.languageID);
                    
                    setLanguageInfo(translation.languageID);
                    
                    // Get the version information strings for this language-codepage combination
                    String subBlockPrefix = "\\StringFileInfo\\" + languageID + codepageID + "\\";
                    setFileInfoStrings(data, subBlockPrefix);
                }
        	}
        }
    }
    
    // obtains the version information strings from the StringFileInfo block
    private void setFileInfoStrings(byte[] data, String subBlockPrefix) {
    	comments 		= getPropertyValue(data, subBlockPrefix + "Comments");
    	internalName 	= getPropertyValue(data, subBlockPrefix + "InternalName");
    	productName 	= getPropertyValue(data, subBlockPrefix + "ProductName");
    	
    	companyName 	= getPropertyValue(data, subBlockPrefix + "CompanyName");
    	legalCopyright 	= getPropertyValue(data, subBlockPrefix + "LegalCopyright");
    	productVersion 	= getPropertyValue(data, subBlockPrefix + "ProductVersion");
    	
    	fileDescription = getPropertyValue(data, subBlockPrefix + "FileDescription");
    	legalTrademarks = getPropertyValue(data, subBlockPrefix + "LegalTrademarks");
    	privateBuild 	= getPropertyValue(data, subBlockPrefix + "PrivateBuild");
    	
    	fileVersion 	= getPropertyValue(data, subBlockPrefix + "FileVersion");
    	originalFilename= getPropertyValue(data, subBlockPrefix + "OriginalFilename");
    	specialBuild 	= getPropertyValue(data, subBlockPrefix + "SpecialBuild");
	}
    
    // queries the specified String version information from the StringFileInfoBlock
    private String getPropertyValue(byte[] data, String subBlock) {
        int[] ptr = new int[1];
        int[] bytesReturned = { 0 };
        boolean result = Version.VerQueryValue(data, subBlock, ptr, bytesReturned);
        String value = "";
        if (result == true)
             value = Util.ptrToString(ptr[0]);
        
        return value;
    }
    
    // gets the language name corresponding to languageID
    private void setLanguageInfo(short languageID) {
        StringBuffer verLanguage = new StringBuffer(256);
        Version.VerLanguageName(languageID, verLanguage, verLanguage.capacity());
        language = verLanguage.toString();
	}

    // obtain and interpret fixed file information
    // converts the fixed file information into human readable strings
	private void setFixedFileInfo(VS_FixedFileInfo fixedFileInfo) {
    	
		// binary file version
        versionMajor = hiword(fixedFileInfo.dwFileVersionMS);
        versionMinor = loword(fixedFileInfo.dwFileVersionMS);
        versionBuild = hiword(fixedFileInfo.dwFileVersionLS);
        versionPrivatePart = loword(fixedFileInfo.dwFileVersionLS);
        if (versionMajor == 0)
                fileVersionString = "";
        else
                fileVersionString =   versionMajor + "."+ versionMinor + "." + versionBuild + "." + versionPrivatePart;

        // boolean attributes of the file
        if ((fixedFileInfo.dwFileFlags & VS_FF_DEBUG) == VS_FF_DEBUG) {
                fileAttributes = "Debug build";
        }
        if ((fixedFileInfo.dwFileFlags & VS_FF_PRERELEASE) == VS_FF_PRERELEASE) { 
                fileAttributes += "Prerelease build";		
        }
        if ((fixedFileInfo.dwFileFlags & VS_FF_PATCHED) == VS_FF_PATCHED) { 
                fileAttributes += "Patched build";			
        }
        if ((fixedFileInfo.dwFileFlags & VS_FF_PRIVATEBUILD) == VS_FF_PRIVATEBUILD) { 
                fileAttributes += "Private build";		
        }
        if ((fixedFileInfo.dwFileFlags & VS_FF_INFOINFERRED) == VS_FF_INFOINFERRED) { 
                fileAttributes += "Info build";		
        }
        if ((fixedFileInfo.dwFileFlags & VS_FF_SPECIALBUILD) == VS_FF_SPECIALBUILD) { 
                fileAttributes += "Special build";		
        }

        // operating system for which this file was designed
        switch (fixedFileInfo.dwFileOS)
        {
        case VOS_DOS:
                fileOS = "MS-DOS";
                break;
        case VOS_DOS_WINDOWS16:
                fileOS = "16-bit Windows on DOS (Windows 3.0, 3.1)";
                break;
        case VOS_DOS_WINDOWS32:
                fileOS = "32-bit Windows on DOS (Win32s)";
                break;
        case VOS_OS216_PM16:
                fileOS = "16-bit Presentation Manager on 16 bit OS/2";
                break;
        case VOS_OS232_PM32:
                fileOS = "32-bit Presentation Manager on 32 bit OS/2";
                break;
        case VOS_NT:
                fileOS = "Windows NT";
                break;
        case VOS__WINDOWS32:
                fileOS = "32-bit Windows";
                break;
        case VOS_NT_WINDOWS32:
                fileOS = "Win32 API on Windows NT";
                break;
        default:
                fileOS = "Unknown";
        }

        // type of file
        switch (fixedFileInfo.dwFileType) {
              case VFT_APP:
                 fileType = "Application";
                 break;
                 
              case VFT_DLL:
                 fileType = "Dynamic link library";
                 break;
                 
              case VFT_DRV:
                 fileType = "Driver";
                 switch (fixedFileInfo.dwFileSubtype) {
                    case VFT2_DRV_PRINTER:
                       fileSubType = "Printer driver";
                    case VFT2_DRV_KEYBOARD:
                       fileSubType = "Keyboard driver";
                    case VFT2_DRV_LANGUAGE:
                       fileSubType = "Language driver";
                    case VFT2_DRV_DISPLAY:
                       fileSubType = "Display driver";
                    case VFT2_DRV_MOUSE:
                       fileSubType = "Mouse driver";
                    case VFT2_DRV_NETWORK:
                       fileSubType = "Network driver";
                    case VFT2_DRV_SYSTEM:
                       fileSubType = "System driver";
                    case VFT2_DRV_INSTALLABLE:
                       fileSubType = "Installable driver";
                    case VFT2_DRV_SOUND:
                       fileSubType = "Sound driver";
                    case VFT2_DRV_COMM:
                       fileSubType = "Comm driver";
                    case VFT2_UNKNOWN:
                       fileSubType = "Unknown";
                 }
                 break;
                 
              case VFT_FONT:
                 fileType = "Font";
                 switch (fixedFileInfo.dwFileSubtype) {
                    case VFT2_FONT_RASTER:
                       fileSubType = "Raster Font";
                    case VFT2_FONT_VECTOR:
                       fileSubType = "Vector Font";
                    case VFT2_FONT_TRUETYPE:
                       fileSubType = "TrueType Font";
                 }
                 break;
                 
              case VFT_VXD:
                 fileType = "Virtual device driver";
                 break;
                     
              case VFT_STATIC_LIB:
                 fileType = "Static link library";
                 break;
                 
              default:
                 fileType = "Unknown";
        }		
	}
	
	// low 16 bits of a 32 bit int
    private int loword(int i) {
    	return i & 0x0000ffff;
    }

    // high 16 bits of a 32 bit int
    private int hiword(int i) {
        return (i & 0xffff0000) >> 16;
    }
    
    //  returns a String representation of the version information obtained for this file
    @Override
    public String toString() {
    	String versionInfo = 
          "Version           : " + fileVersionString + "\n"
      	+ "Attributes        : " + fileAttributes + "\n"
    	+ "Operating System  : " + fileOS + "\n"
    	
    	+ "Type              : " + fileType + "\n"
    	+ "SubType           : " + fileSubType + "\n"
    	+ "Language          : " + language + "\n"
    	
    	+ "Comments          : " + comments + "\n"
    	+ "Internal Name     : " + internalName + "\n"
    	+ "Product Name      : " + productName + "\n"
    	
    	+ "Company Name      : " + companyName + "\n"
    	+ "Legal Copyright   : " + legalCopyright + "\n"
    	+ "Product Version   : " + productVersion + "\n"
    	
    	+ "File Description  : " + fileDescription + "\n"
    	+ "Legal Trademarks  : " + legalTrademarks + "\n"
    	+ "Private Build     : " + privateBuild + "\n"
    	
    	+ "File Version      : " + fileVersion + "\n"
    	+ "Original Filename : " + originalFilename + "\n"
    	+ "Special Build     : " + specialBuild + "\n";
    	return versionInfo;
    }

    // main method - optionally takes a path to an exe or dll 
	// for which to obtain and display version information
    public static void main(String[] args) {
    	String path = "C:\\Windows\\Notepad.exe";
    	if (args.length == 1)
    		path = args[0];
    
		VersionInfo fvi = new VersionInfo(path);
		System.out.println(fvi);
	}
}
