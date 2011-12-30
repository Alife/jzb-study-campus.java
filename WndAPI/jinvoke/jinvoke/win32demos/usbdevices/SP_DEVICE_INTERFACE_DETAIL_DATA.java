package jinvoke.win32demos.usbdevices;

import com.jinvoke.Embedded;
import com.jinvoke.NativeStruct;

@NativeStruct
public class SP_DEVICE_INTERFACE_DETAIL_DATA {
	public int cbSize;
    @Embedded(length=256)
    public StringBuffer DevicePath = new StringBuffer(256);
}
