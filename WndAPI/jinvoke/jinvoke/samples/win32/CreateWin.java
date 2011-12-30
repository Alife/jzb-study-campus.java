package jinvoke.samples.win32;

import com.jinvoke.Callback;
import com.jinvoke.Util;
import com.jinvoke.win32.Gdi32;
import com.jinvoke.win32.User32;
import com.jinvoke.win32.WinConstants;
import com.jinvoke.win32.structs.Msg;
import com.jinvoke.win32.structs.PaintStruct;
import com.jinvoke.win32.structs.WndClassEx;

public class CreateWin
{
    static public void main(String[] arg)
    {
        // Register window class
        WndClassEx wc = new WndClassEx();
        wc.cbSize = Util.getStructSize(wc);
        wc.lpszClassName = "CreateWin";
        wc.lpszMenuName= "CreateWin";
        wc.hbrBackground = WinConstants.COLOR_WINDOW;
        wc.lpfnWndProc = new Callback(CreateWin.class, "WndProc");
        if (0 == User32.RegisterClassEx(wc))
            System.out.println("RegisterClassEx failed!");

        // Create top level "frame" window
        int hwnd = User32.CreateWindowEx(0, "CreateWin", "CreateWin!", WinConstants.WS_OVERLAPPEDWINDOW,
                                  100, 100, 210, 200, 0, 0, 0, 0);
        if (hwnd == 0)
        {
            System.out.println("CreateWindowEx failed!");
        }

        // Show the window
        User32.ShowWindow(hwnd, 1);

        // Standard message dispatch loop 
        Msg msg = new Msg();
        while (User32.GetMessage(msg, 0, 0, 0))
        {
        	User32.TranslateMessage(msg);
        	User32.DispatchMessage(msg);
        }
    }

    public static int WndProc(int hwnd, int msg, int w, int l)
    {
        switch (msg)
        {
            case WinConstants.WM_PAINT:
                {
                    PaintStruct ps = new PaintStruct();
                    int hdc = User32.BeginPaint(hwnd, ps);
                    String s = "Hello World from J/Invoke!";
                    Gdi32.SetBkMode(hdc, 1); // TRANSPARENT
                    Gdi32.ExtTextOut(hdc, 10, 10, 0, null, s, s.length(), null);
                    User32.EndPaint(hwnd, ps);
                }
                break;

            case WinConstants.WM_DESTROY:
            	User32.PostQuitMessage(0);
                break;
        }
        return User32.DefWindowProc(hwnd, msg, w, l);
    }
    
}




