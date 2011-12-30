package jinvoke.samples.win32;

import java.awt.Button;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.Util;
import com.jinvoke.win32.Gdi32;
import com.jinvoke.win32.User32;

public class ShapedJavaWindow {

	  public static final int CC_RGBINIT    = 0x00000001;
	  public static final int CC_FULLOPEN   = 0x00000002;
	  public static final int CC_ENABLEHOOK = 0x00000010;

	  public static final int WM_LBUTTONUP  = 0x0202;
	  public static final int WM_CHAR       = 0x0102;
	  
	  @NativeImport(library="COMDLG32")
	  public static native boolean ChooseColor (CHOOSECOLOR pChoosecolor);
	  
	  @NativeImport(library="comctl32")
	  public static native void InitCommonControls();
	  
	public static void main(String[] args) {
		JInvoke.initialize();

		InitCommonControls();
		final Frame f = new Frame();
		Button btn = new Button("Press to make this Java window get an elliptic shape...");
		
		btn.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {

				int hwnd = Util.getWindowHandle(f);
				int winWidth = f.getWidth();
				int winHeight = f.getHeight();
				int regionHandle = Gdi32.CreateEllipticRgn(1, 1, winWidth, winHeight);

				User32.SetWindowRgn(hwnd, regionHandle, true);
				
				System.out.println("hwnd of Java window is "+hwnd);
			}});

		f.add(btn, "Center");
		f.pack();
		f.setBounds(100, 100, 600, 300);
		f.setVisible(true);
	}

}
