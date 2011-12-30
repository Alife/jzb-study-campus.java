package jinvoke.samples.win32;
import com.jinvoke.JInvoke;
import com.jinvoke.Util;
import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.structs.Overlapped;


import static com.jinvoke.win32.WinConstants.*;

public class OverlappedIO {
	
	public static void main(String[] args) {
		JInvoke.initialize();
		
		int mDevHandle = Kernel32.CreateFile("C:\\jinvoke.lic", GENERIC_READ,
				0, null, OPEN_EXISTING, FILE_FLAG_OVERLAPPED, 0);
		
		if (mDevHandle == INVALID_HANDLE_VALUE)
			System.out.println("Error opening file.");
		else
			System.out.println("handle="+mDevHandle);
		
		int dataReadyEvent = Kernel32.CreateEvent(null, true, false, null);
		
		Overlapped overlap = new Overlapped();
		overlap.hEvent = dataReadyEvent;
		
		int dwRead[] = new int[1];
        byte[] buf = new byte[10]; 
        
        int bufptr = Util.byteArrayToPtr(buf);
                        
        Kernel32.ReadFile(mDevHandle, bufptr, buf.length, dwRead, overlap);
         
        
         //wait for the event   		
   		int result = Kernel32.WaitForSingleObject(dataReadyEvent, 500);
   		switch (result) {
   		case WAIT_OBJECT_0:
   			System.out.println("WAIT_OBJECT_0");
   			break;
   			
   		case WAIT_TIMEOUT:
   			System.out.println("Timed out");
   			break;
   			
   		default:
   			System.out.println("None of the above...");
   		
   		}
        int[] dwBytesRead = new int[1];
        Kernel32.GetOverlappedResult(mDevHandle, overlap, dwBytesRead, true);
        System.out.println("Bytes read="+dwBytesRead[0]);
        byte[] ba = Util.ptrToByteArray(bufptr, 10);
		
        System.out.println(new String(ba));
		
		Kernel32.CloseHandle(mDevHandle);
		
	}
}
