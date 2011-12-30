package jinvoke.samples.win32;

import static com.jinvoke.win32.WinConstants.*;

import com.jinvoke.Callback;
import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.Util;
import com.jinvoke.win32.Gdi32;
import com.jinvoke.win32.User32;
import com.jinvoke.win32.WinConstants;
import com.jinvoke.win32.structs.Msg;
import com.jinvoke.win32.structs.PaintStruct;
import com.jinvoke.win32.structs.Point;
import com.jinvoke.win32.structs.WndClassEx;

public class WindowWithHoles {
	public static int winWidth = 400;
	public static int winHeight = 200;

	public static int hwnd;

	@NativeImport(library = "Gdi32")
	public static void main(String[] arg) {
		JInvoke.initialize();

		WndClassEx wc = new WndClassEx();
		wc.cbSize = Util.getStructSize(wc);
		wc.lpszClassName = "CreateWin";
		wc.lpszMenuName = "CreateWin";
		wc.hbrBackground = Gdi32.GetStockObject(WinConstants.GRAY_BRUSH);
		wc.lpfnWndProc = new Callback(WindowWithHoles.class, "WndProc");

		if (0 == User32.RegisterClassEx(wc))
			System.out.println("RegisterClassEx failed!");

		hwnd = User32.CreateWindowEx(0, "CreateWin", "CreateWin!", WS_POPUP, 
				100, 100, winWidth, winHeight, 0, 0, 0, 0);

		if (hwnd == 0)
			System.out.println("CreateWindowEx failed!");

		CreateButton(hwnd, winWidth, winHeight);
		// Show the window
		User32.ShowWindow(hwnd, 1);

		CutHoles(hwnd, winWidth, winHeight);

		// Standard message dispatch loop
		Msg msg = new Msg();
		while (User32.GetMessage(msg, 0, 0, 0)) {
			User32.TranslateMessage(msg);
			User32.DispatchMessage(msg);
		}
	}

	static void CreateButton(int parentWinHandle, int winWidth, int winHeight) {
		int buttonHandle = User32.CreateWindowEx(0, "Button", "Close",
				WS_CHILD, winWidth * 2 / 3, winHeight * 3 / 4, 50, 25,
				parentWinHandle, 0, 0, 0);
		User32.SetParent(buttonHandle, parentWinHandle);
		User32.ShowWindow(buttonHandle, SW_NORMAL);
	}

	public static void CutHoles(int hwnd, int winWidth, int winHeight) {
		int formRegion = Gdi32.CreateRectRgn(0, 0, winWidth, winHeight);

		formRegion = CutHolesRecursively(formRegion, 3, 0, 0, winWidth, winHeight);

		// Restrict the window to the region.
		User32.SetWindowRgn(hwnd, formRegion, true);
	}

	public static int CutHolesRecursively(int outerRegion, int level, int xmin,
			int ymin, int xmax, int ymax) {
		int innerRegion;
		int combinedRegion;
		int dx, dy, x1, x2, y1, y2;
	
		if (level < 1)
			return outerRegion;
	
		dx = (xmax - xmin) / 3;
		x1 = xmin + dx;
		x2 = xmin + 2 * dx;
		dy = (ymax - ymin) / 3;
		y1 = ymin + dy;
		y2 = ymin + 2 * dy;
	
		innerRegion = Gdi32.CreateEllipticRgn(xmin, ymin, x1, y1);
		combinedRegion = Gdi32.CreateRectRgn(0, 0, x1, y1);
		Gdi32.CombineRgn(combinedRegion, outerRegion, innerRegion, RGN_DIFF);
		Gdi32.DeleteObject(outerRegion);
		Gdi32.DeleteObject(innerRegion);
		outerRegion = combinedRegion;
				
		innerRegion = Gdi32.CreateEllipticRgn(x2, ymin, xmax, y1);
		combinedRegion = Gdi32.CreateRectRgn(0, 0, 0, 0);
		Gdi32.CombineRgn(combinedRegion, outerRegion, innerRegion, RGN_DIFF);
		Gdi32.DeleteObject(outerRegion);
		Gdi32.DeleteObject(innerRegion);
		outerRegion = combinedRegion;
	
		innerRegion = Gdi32.CreateEllipticRgn(x1, y1, x2, y2);
		combinedRegion = Gdi32.CreateRectRgn(0, 0, 0, 0);
		Gdi32.CombineRgn(combinedRegion, outerRegion, innerRegion, RGN_DIFF);
		Gdi32.DeleteObject(outerRegion);
		Gdi32.DeleteObject(innerRegion);
		outerRegion = combinedRegion;
	
		innerRegion = Gdi32.CreateEllipticRgn(xmin, y2, x1, ymax);
		combinedRegion = Gdi32.CreateRectRgn(0, 0, 0, 0);
		Gdi32.CombineRgn(combinedRegion, outerRegion, innerRegion, RGN_DIFF);
		Gdi32.DeleteObject(outerRegion);
		Gdi32.DeleteObject(innerRegion);
		outerRegion = combinedRegion;
	
		innerRegion = Gdi32.CreateEllipticRgn(x2, y2, xmax, ymax);
		combinedRegion = Gdi32.CreateRectRgn(0, 0, 0, 0);
		Gdi32.CombineRgn(combinedRegion, outerRegion, innerRegion, RGN_DIFF);
		Gdi32.DeleteObject(outerRegion);
		Gdi32.DeleteObject(innerRegion);
		outerRegion = combinedRegion;
	
		// Recursively cut more holes
		outerRegion = CutHolesRecursively(outerRegion, level - 1, x1, ymin, x2, y1);
		outerRegion = CutHolesRecursively(outerRegion, level - 1, xmin, y1, x1, y2);
		outerRegion = CutHolesRecursively(outerRegion, level - 1, x2, y1, xmax, y2);
		outerRegion = CutHolesRecursively(outerRegion, level - 1, x1, y2, x2, ymax);
	
		return outerRegion;
	}
	public static int WndProc(int hwnd, int msg, int w, int l)
    {
    	final int IDM_END = WM_APP + 3;
        switch (msg)
        {
            case WM_PAINT: //0x0f: // WM_PAINT
                {
                    PaintStruct ps = new PaintStruct();
                    int hdc = User32.BeginPaint(hwnd, ps);
                    String s = "Hello World";
                    WindowWithHoles.CutHoles(WindowWithHoles.hwnd, WindowWithHoles.winWidth, WindowWithHoles.winHeight);
                    Gdi32.SetBkMode(hdc, 1); // TRANSPARENT
                    Gdi32.SetBkColor(hdc, 0x00ff0000);
                    Gdi32.ExtTextOut(hdc, 10, 10, 0, null, s, s.length(), null);
                    User32.EndPaint(hwnd, ps);

                }
                break;
                
            case WM_LBUTTONDOWN:
            	User32.SendMessage(hwnd, WM_SYSCOMMAND, SC_MOVE | 2, 0);
                break;
            
                
            case WM_COMMAND:
                switch(w){
                case BN_CLICKED:
                case IDM_END:
                	//if (l == buttonHandle)
                    User32.SendMessage(hwnd, WM_CLOSE, 0, 0);
                    break;
                }
                break;
                
            case WM_RBUTTONDOWN:
            	 Point pos = new Point();
                 int hMenu = User32.CreatePopupMenu();
                 User32.AppendMenu(hMenu, MF_STRING, IDM_END, "Close");
                 User32.GetCursorPos(pos);
                 User32.TrackPopupMenu(hMenu, TPM_LEFTALIGN | TPM_LEFTBUTTON,
                    pos.x, pos.y, 0, hwnd, null);
                 User32.DestroyMenu(hMenu);
                 break;

                
            case WM_DESTROY:
            	User32.PostQuitMessage(0);
                break;
                
        }
        return User32.DefWindowProc(hwnd, msg, w, l);
    }
}
