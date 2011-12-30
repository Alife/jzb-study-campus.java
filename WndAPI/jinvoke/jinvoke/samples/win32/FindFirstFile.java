package jinvoke.samples.win32;

import java.io.IOException;

import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.win32.structs.Win32FindData;

public class FindFirstFile {
	
	@NativeImport(library="kernel32")
	public static native int FindFirstFileW(String lpFileName, Win32FindData lpFindFileData);

 	@NativeImport(library="kernel32")
 	public static native int FindNextFileW(int handle, Win32FindData lpFindFileData);

 	@NativeImport(library="kernel32")
 	public static native int FindClose(int handle);

	public static void main(String[] args) throws Exception {
		JInvoke.initialize();
	   	int[] files = { 0 };
    	int[] folders = { 0 };
    	RecurseDirectory("C:\\Windows", -1, files, folders, 0);
    	System.out.println("files: "+files[0]+",  folders:"+folders[0]);
	}
	
	private static long RecurseDirectory(String directory, int level, int []files, int []folders, int depth) throws IllegalArgumentException, IOException, IllegalAccessException {
        int INVALID_HANDLE_VALUE = -1;
        long size = 0;
        files[0] = 0;
        folders[0] = 0;
        Win32FindData findData = new Win32FindData();

        int findHandle;

        findHandle = FindFirstFileW("\\\\?\\" + directory + "\\*",  findData);
        
        if (findHandle != INVALID_HANDLE_VALUE) {
            do {
        	    if ((findData.dwFileAttributes &  0x00000010) != 0) {
                	 if (!(findData.cFileName.toString().equals(".")) && !(findData.cFileName.toString().equals(".."))) {
                             	
                        folders[0]++;
                        String subdirectory = directory + (directory.endsWith("\\") ? "" : "\\") + new String(findData.cFileName);
                        if (level != 0)  
                        {
                        	int[] subfiles = { 0 };
                        	int[] subfolders = { 0 };
                        	for (int i = 0; i < depth; i++) 
                    			System.out.print(" ");
                            System.out.println("["+new String(findData.cFileName)+"]");
	                        size += RecurseDirectory(subdirectory, level - 1,  subfiles, subfolders, depth++);
	
	                        folders[0] += subfolders[0];
	                        files[0] += subfiles[0];
                        }
                    }
                }
                else 
                {
                	for (int i = 0; i < depth; i++) 
        			System.out.print(" ");
                	System.out.println(new String(findData.cFileName));
                    // File
                    files[0]++;
                    size += (long)findData.nFileSizeLow + (long)findData.nFileSizeHigh * 4294967296l;
                }
            } 
            while (0 != FindNextFileW(findHandle, findData));
            FindClose(findHandle);

        }

        return size;
    }


}
