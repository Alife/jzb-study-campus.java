package jinvoke.samples.win32;

import com.jinvoke.Util;
import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.User32;
import com.jinvoke.win32.WinConstants;
import com.jinvoke.win32.structs.ProcessInformation;
import com.jinvoke.win32.structs.SecurityAttributes;
import com.jinvoke.win32.structs.StartupInfo;

public class StartProcess {
	public static void main (String[] args){
	ProcessInformation procInfo = new ProcessInformation();
	StartupInfo startupInfo = new StartupInfo();
	startupInfo.cb = Util.getStructSize(startupInfo); 
	System.out.println(startupInfo.cb );
	SecurityAttributes secAtt = new SecurityAttributes();
	SecurityAttributes threadAtt = new SecurityAttributes();
	boolean result = Kernel32.CreateProcess(null, 
			"Calc.exe", secAtt, threadAtt, true, WinConstants.NORMAL_PRIORITY_CLASS, 0, "C:\\Windows\\System32", startupInfo, procInfo);
	System.out.println(result);
	User32.MessageBox(0, "Click OK to close Calculator", "J/Invoke MessageBox", 0);
	result = Kernel32.TerminateProcess(procInfo.hProcess,0);
	result = Kernel32.CloseHandle(procInfo.hThread);
	result = Kernel32.CloseHandle(procInfo.hProcess);
	}
}
