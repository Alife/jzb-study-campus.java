package jinvoke.samples.win32;

import com.jinvoke.win32.Shell32;
import com.jinvoke.win32.User32;

import static com.jinvoke.win32.WinConstants.*;

public class ShellExecute {

	public static void main(String args[]) throws Exception
	{
		int screenHdc = User32.GetDesktopWindow();
		Shell32.ShellExecute(screenHdc, "Open", "C:\\Windows\\Notepad.exe", "", "C:\\", SW_SHOWMAXIMIZED);
		//Shell32.ShellExecute(screenHdc,"Open","mailto:user@hotmail.com?subject=test","","",1);
	}
}
