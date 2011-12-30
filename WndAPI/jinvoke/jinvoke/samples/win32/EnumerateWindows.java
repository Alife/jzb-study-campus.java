package jinvoke.samples.win32;

import com.jinvoke.Callback;
import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;

public class EnumerateWindows {

	@NativeImport(library = "user32")
	public static native int EnumWindows(Callback callPtr, String lPar);

	@NativeImport(library = "user32")
	public static native int GetWindowText(int hWnd, StringBuffer sb, int nMaxCount);

	public static void main(String[] args) {
		JInvoke.initialize();

		Callback callback = new Callback(EnumerateWindows.class, "EnumWindowsProc");
		EnumWindows(callback, "data");
	}

	public static boolean EnumWindowsProc(int hwnd, String lParam) {
		StringBuffer sb = new StringBuffer(128);
		GetWindowText(hwnd, sb, sb.capacity());
		System.out.println("Window handle=" + hwnd + ", lParam=" + lParam+ ", text=" + sb);
		return true;
	}

}
