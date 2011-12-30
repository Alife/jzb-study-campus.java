package jinvoke.samples.win32;

import static com.jinvoke.win32.WinConstants.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.jinvoke.win32.Kernel32;

public class FileChangeNotification {
	public static void main(String[] args) {
		String directory 	= "C:\\"; // replace this with the path to watch for directory changes
		
		int dirHandle = Kernel32.CreateFile(directory, FILE_LIST_DIRECTORY, 
				FILE_SHARE_READ,// + FILE_SHARE_DELETE + FILE_SHARE_WRITE, 
				null, 
				OPEN_EXISTING, FILE_FLAG_BACKUP_SEMANTICS, 0);
		if (dirHandle == 0) {
			System.out.println("CreateFile Failed");
		}
		
		int BUFSIZE = 2048;
		byte[] buf = new byte[2048]; 
		int[] bytesReturned = new int [1];
		while(true)
		if (Kernel32.ReadDirectoryChangesW(dirHandle, buf, BUFSIZE, true, FILE_NOTIFY_CHANGE_FILE_NAME | 
		         FILE_NOTIFY_CHANGE_DIR_NAME | FILE_NOTIFY_CHANGE_LAST_WRITE, bytesReturned, null, null))
		{
			if (bytesReturned[0] != 0)
			{
				System.out.println("Buffer len = "+buf.length);
				ByteBuffer bb = ByteBuffer.wrap(buf);
				bb.order(ByteOrder.LITTLE_ENDIAN);
				
				while (true) 
				{
					
					int nextEntryOffset = bb.getInt();
					int action = bb.getInt();
	
				    int fileLen = bb.getInt();
	
				    System.out.println("nextEntryOffset = " +nextEntryOffset);
				    System.out.println("File len = " +fileLen);
				    byte [] stringbytes = new byte[fileLen];
				    char [] stringchars = new char[fileLen / 2];
				      
				    bb.get(stringbytes);
				      
				    for (int i = 0; i < stringchars.length; i++) {
						stringchars[i] = (char) (stringbytes[2*i] + stringbytes[2*i+1]) ;
					}
				    fileAction(action, new String(stringchars) );
	
			        if (nextEntryOffset == 0)
			    	  break;
			        else
			    	  bb.position(nextEntryOffset);
				}
			}
		} else {
			System.out.println("ReadDirectoryChangesW failed.");
		}
		
	}		

	public static final void fileAction(int action, String filename)
	{
		switch  (action)
		{
		case FILE_ACTION_ADDED:
			System.out.print("File Added    : ");
			break;
			
		case FILE_ACTION_MODIFIED:
			System.out.print("File Modified : ");
			break;
			
		case FILE_ACTION_REMOVED:				
			System.out.print("File Removed : ");
			break;
			
		case FILE_ACTION_RENAMED_NEW_NAME:
			System.out.print("Rename new   : ");
			break;
			
		case FILE_ACTION_RENAMED_OLD_NAME:
			System.out.print("Rename old   : ");
			break;
			
		default:
			System.out.println("Some other file related event occured.");
		}
		System.out.println(filename);
	}
}