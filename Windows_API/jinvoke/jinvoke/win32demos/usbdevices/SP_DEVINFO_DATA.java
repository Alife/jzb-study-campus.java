package jinvoke.win32demos.usbdevices;

import com.jinvoke.NativeStruct;

@NativeStruct
public class SP_DEVINFO_DATA {
	public int cbSize;
    public GUID ClassGuid = new GUID();
    public int DevInst;
    public int Reserved;
}
