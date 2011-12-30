package jinvoke.samples.win32;

import com.jinvoke.win32.User32;

public class MessageBox {
	public static void main(String args[])
	{
		User32.MessageBox(0, "Hello, world!", "J/Invoke", 0);
	}
}
