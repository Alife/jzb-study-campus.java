package jinvoke.win32demos.usbdevices;

import com.jinvoke.NativeStruct;

@NativeStruct
public class SP_DEVICE_INTERFACE_DATA {
	public int cbSize;
    public GUID InterfaceClassGuid = new GUID();
    public int Flags;
    public int Reserved;
}
