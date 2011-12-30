package jinvoke.samples.win32;
import com.jinvoke.win32.User32;
import com.jinvoke.win32.WinConstants;

public class ShowHideWindow {
	public static void main(String args[])
	{
		int i = User32.FindWindow(null, "Untitled - Notepad"); 
		if (i != 0)
			User32.ShowWindow(i, WinConstants.SW_HIDE);
		else
			System.out.println("No window found with title 'Untitled - Notepad'.");
	}
}