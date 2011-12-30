package jinvoke.samples.win32;

import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.Util;

public class ChooseColorSample {

	public static final int CC_RGBINIT = 0x00000001;
	public static final int CC_FULLOPEN = 0x00000002;
	public static final int CC_ENABLEHOOK = 0x00000010;
	public static final int WM_LBUTTONUP = 0x0202;
	public static final int WM_CHAR = 0x0102;

	@NativeImport(library = "COMDLG32")
	public static native boolean ChooseColor(CHOOSECOLOR pChoosecolor);

	@NativeImport(library = "comctl32")
	public static native void InitCommonControls();

	public static void main(String[] args) {
		JInvoke.initialize();

		InitCommonControls();
		CHOOSECOLOR cc = new CHOOSECOLOR();
		int size = Util.getStructSize(cc.getClass());
		cc.lStructSize = size;

		cc.hwndOwner = 0;
		cc.hInstance = 0;
		cc.rgbResult = 0;
		
		// set the initial custom colors
		cc.lpCustColors = new int[16];
		for (int i = 0; i < 16; i++)
			cc.lpCustColors[i] = 0x00FFFFFF;
		
		cc.flags = CC_RGBINIT | CC_FULLOPEN;

		// Call the API
		ChooseColor(cc);

		// read back the custom colors
		// these could have been modified by the user
		System.out.println("Colors added to the custom colors list: ");
		for (int i = 0; i < 16; i++)
			System.out.println("cc.lpCustColors[" + i + "] = " + Integer.toHexString(cc.lpCustColors[i]));

	}

}
