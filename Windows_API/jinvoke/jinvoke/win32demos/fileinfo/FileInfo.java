// FileInfo.java
// Gayathri Singh, March 2008, gayathri@byteblend.com

/* This class encapsulates file information for a specified file.
   The file information is obtained using Shell and Kernel APIs.
   
   Shell related information is obtained using Shell32.SHGetFileInfo and
   Kernel related information is obtained using Kernel32.GetFileInformationByHandle.
   
   An instance of this class is created by constructing a FileInfo object, passing in 
   the path to the file whose information is to be obtained. The file information can
   then be queried using the public fields of this class.
   
   This class is designed to be used independently, and can be run as a console application.
   The FileInfoUI class provides a GUI frontend for this class.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.fileinfo.FileInfo  [ file-path ]
*/

package jinvoke.win32demos.fileinfo;

import static com.jinvoke.win32.WinConstants.*;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.Icon;

import com.jinvoke.Util;
import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.Shell32;
import com.jinvoke.win32.User32;
import com.jinvoke.win32.structs.ByHandleFileInformation;
import com.jinvoke.win32.structs.FileTime;
import com.jinvoke.win32.structs.ShFileInfo;
import com.jinvoke.win32.structs.SystemTime;

public class FileInfo {
	
	// file information obtained using Shell32's SHGetFileInfo API
	public Icon icon;
	public String fileName;
	public String typeName;
	
	// file information obtained using Kernel32's GetFileInformationByHandle API
	public boolean isArchive;
	public boolean isCompressed;
	public long compressedFileSize;
	public boolean isHidden;
	public boolean isSystemFile;
	public boolean isReadOnly;
	public boolean isTempFile;
	public Date createdTime;
	public Date modifiedTime;
	public Date accessedTime;
	public int  volumeSerialNumber;
	public long fileSize;
	public long uniqueIdentifier;
	public int numLinks;
	

	// constructor
	public FileInfo(String filePath) {
		getShellFileInfo(filePath);
		getFileInformationByHandle(filePath);
	}

	// obtains file information (icon, display name and type name) using Shell API
	private void getShellFileInfo(String filePath) {
		ShFileInfo shInfo = new ShFileInfo();
		int basicShFlags = SHGFI_LARGEICON | SHGFI_ICON | SHGFI_DISPLAYNAME | SHGFI_TYPENAME;
		
		// call the Shell32 SHGetFileInfo API
		Shell32.SHGetFileInfo(filePath, 0, shInfo, Util.getStructSize(shInfo), basicShFlags);
		
		// set the file icon 
		int hIcon = shInfo.hIcon;
		icon = Util.getIcon(hIcon, 32);
		User32.DestroyIcon(hIcon);
		
		// set the display name 
		fileName = shInfo.szDisplayName.toString().trim();
		
		// set the type name
		typeName = shInfo.szTypeName.toString().trim();
		if (typeName.equals("")) {
			String extension = "";
			int i = fileName.lastIndexOf('.');
			if (i > 0 && i < fileName.length() - 1) {
				extension = fileName.substring(i + 1);
			}
			typeName = extension.toUpperCase() + " File";
		}
	}
	
	// obtains file information using Kernel32.GetFileInformationByHandle
	private void getFileInformationByHandle(String filePath) {
		// initialize the ByHandleFileInformation struct
		// this will be populated by the Kernel32.GetFileInformationByHandle method
		ByHandleFileInformation fileInfo = new ByHandleFileInformation();
		fileInfo.ftCreationTime = new FileTime();
		fileInfo.ftLastAccessTime = new FileTime();
		fileInfo.ftLastWriteTime = new FileTime();
		
		int fileHandle;
		
		// open the file and get a file handle - to be passed 
		// in the Kernel32.GetFileInformationByHandle method
		fileHandle = Kernel32.CreateFile(filePath, 0, FILE_SHARE_READ | FILE_SHARE_WRITE, 
				null, OPEN_EXISTING, 0, 0);
		
		// call the GetFileInformationByHandle API
		Kernel32.GetFileInformationByHandle(fileHandle, fileInfo);

		// obtain the file size
		// fileSize is a 64-bit long - enough to hold file sizes above 4GB (2 ^ 32 bytes)
		// first, we get the high-order 32 bits from fileInfo.nFileSizeHigh
		// then, we left-shift the bits by 32 bits and add the low-order 32 bits
		fileSize = fileInfo.nFileSizeHigh;
		fileSize = (fileSize << 32) + fileInfo.nFileSizeLow;
		
		// obtain file attributes
		if ((fileInfo.dwFileAttributes & FILE_ATTRIBUTE_ARCHIVE) != 0) {
			isArchive = true;
		}
		if ((fileInfo.dwFileAttributes & FILE_ATTRIBUTE_COMPRESSED) != 0) {
			int[] compFileSizeHigh = { 0 };
			compressedFileSize = Kernel32.GetCompressedFileSize(filePath, compFileSizeHigh);
			compressedFileSize += (compFileSizeHigh[0] << 32);
		} else {
			compressedFileSize = fileSize;
		}
		
		if ((fileInfo.dwFileAttributes & FILE_ATTRIBUTE_HIDDEN) != 0) 
			isHidden = true;
		
		if ((fileInfo.dwFileAttributes & FILE_ATTRIBUTE_READONLY) != 0) 
			isReadOnly = true;
		
		if ((fileInfo.dwFileAttributes & FILE_ATTRIBUTE_SYSTEM) != 0) 
			isSystemFile = true;
		
		if ((fileInfo.dwFileAttributes & FILE_ATTRIBUTE_TEMPORARY) != 0) 
			isTempFile = true;
		
		// obtain the file creation, modification and last accessed times
		createdTime  = filetimeToDate(fileInfo.ftCreationTime);
		modifiedTime = filetimeToDate(fileInfo.ftLastWriteTime);
		accessedTime = filetimeToDate(fileInfo.ftLastAccessTime);
		
		// obtain volume serial number...
		volumeSerialNumber = fileInfo.dwVolumeSerialNumber;
		
		// ... and 64-bit unique identifier
		uniqueIdentifier = fileInfo.nFileIndexHigh;
		uniqueIdentifier = (uniqueIdentifier << 32) + fileInfo.nFileIndexLow;
		
		// number of links to the file
		numLinks = fileInfo.nNumberOfLinks;
		
		// finally, close the file handle
		if (fileHandle > 0) {
			Kernel32.CloseHandle(fileHandle);
		}
	}

	// utility method that converts time from 
	// FileTime format to java's Date format 
	private Date filetimeToDate(FileTime ftTime) {
		FileTime localFileTime = new FileTime();
		SystemTime sysTime = new SystemTime();

		// convert the filetime to local system time to account 
		// for the current time zone and daylight saving time
		Kernel32.FileTimeToLocalFileTime(ftTime, localFileTime);
		
		// convert the local file time to SystemTime struct
		// this provides us with easy to use fields for constructing GregorianCalendar next
		Kernel32.FileTimeToSystemTime(localFileTime, sysTime);
		
		// create an instance of GregorianCalendar using the SystemTime fields
		GregorianCalendar gc = new GregorianCalendar(sysTime.wYear, 
				sysTime.wMonth - 1, // month is 0-based in Java 
				sysTime.wDay, sysTime.wHour, sysTime.wMinute, sysTime.wSecond);
		
		// get time in java.util.Date format
		return gc.getTime();
	}
	
	// returns a String representation of the file information obtained for this file
	@Override
    public String toString() {
    	String fileInfo = 
          "fileName          : " + fileName + "\n"
      	+ "typeName          : " + typeName + "\n"
    	+ "fileSize          : " + fileSize + "\n"
    	
    	+ "isArchive         : " + isArchive + "\n"	
    	+ "isCompressed      : " + isCompressed + "\n"
    	+ "compressed size   : " + compressedFileSize + "\n"
    	+ "isHidden          : " + isHidden + "\n"
    	
    	+ "isSystemFile      : " + isSystemFile + "\n"
    	+ "isReadOnly        : " + isReadOnly + "\n"
    	+ "isTempFile        : " + isTempFile + "\n"
    	
    	+ "createdTime       : " + createdTime + "\n"
    	+ "modifiedTime      : " + modifiedTime + "\n"
    	+ "accessedTime      : " + accessedTime + "\n"
    	
    	+ "volumeSerialNumber: " + String.format("%08x", volumeSerialNumber) + "\n"
    	+ "uniqueIdentifier  : " + String.format("%016x", uniqueIdentifier) + "\n"
    	+ "numLinks          : " + numLinks + "\n";
    	return fileInfo;
    }
	
	// main method - optionally takes a file path argument 
	// for which to obtain and display file information
    public static void main(String[] args) {
    	String path = "C:\\Windows\\Notepad.exe";
    	if (args.length == 1)
    		path = args[0];
    	
		FileInfo fi = new FileInfo(path);
		System.out.println(fi);
	}
}
