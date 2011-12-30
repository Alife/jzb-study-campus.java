package jinvoke.samples.win32;

import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.WinConstants;

public class GetSystemDirectory {

	public static void main(String s[])
	{
		StringBuffer dir = new StringBuffer(WinConstants.MAX_PATH);
		Kernel32.GetSystemDirectory(dir, WinConstants.MAX_PATH);
		System.out.println(dir.toString());
	}

}
