package jinvoke.win32demos.usbdevices;

//http://www.osronline.com/showThread.cfm?link=30653
import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.Util;
import com.jinvoke.win32.WinConstants;

@NativeImport(library = "setupapi.dll")
public class UsbDevice {
	static final int DIGCF_PRESENT 			= 0x00000002;
	static final int DIGCF_ALLCLASSES 		= 0x00000004;
	static final int DIGCF_PROFILE 			= 0x00000008;
	static final int DIGCF_DEVICEINTERFACE 	= 0x00000010;

	public static native int SetupDiGetClassDevs(GUID classGuid,
			String enumerator, int hwndParent, int flags);

	public static native boolean SetupDiEnumDeviceInterfaces(int h, int i,
			GUID diskGUID, int i2, SP_DEVICE_INTERFACE_DATA dia);

	public static native boolean SetupDiGetDeviceInterfaceDetail(int hDevInfo,
			SP_DEVICE_INTERFACE_DATA deviceInterfaceData,
			SP_DEVICE_INTERFACE_DETAIL_DATA deviceInterfaceDetailData,
			int deviceInterfaceDetailDataSize, int[] requiredSize,
			SP_DEVINFO_DATA deviceInfoData);

	public static native int CM_Get_Device_ID(int dnDevInst, int Buffer,
			int BufferLen, int ulFlags);

	public static native int CM_Get_Parent(int[] pdnDevInst, int dnDevInst,
			int ulFlags);

	public static native boolean SetupDiDestroyDeviceInfoList(int hDevInfo);

	public static void main(String[] args) {
		JInvoke.initialize();

		//GUID_DEVINTERFACE_DISK
		GUID DiskGUID = new GUID();
		DiskGUID.Data1 = 0x53f56307;
		DiskGUID.Data2 = (short) 0xb6bf;
		DiskGUID.Data3 = (short) 0x11d0;
		DiskGUID.Data4 = new byte[] { (byte) 0x94, (byte) 0xf2, (byte) 0x00,
				(byte) 0xa0, (byte) 0xc9, (byte) 0x1e, (byte) 0xfb, (byte) 0x8b };

		// We start at the "root" of the device tree and look for all
		// devices that match the interface GUID of a disk
		int h = SetupDiGetClassDevs(DiskGUID, null, 0, 
				DIGCF_PRESENT | 
				DIGCF_DEVICEINTERFACE);
		if (h != WinConstants.INVALID_HANDLE_VALUE) {
			boolean success = true;
			int i = 0;
			while (success) {
				// create a Device Interface Data structure
				SP_DEVICE_INTERFACE_DATA dia = new SP_DEVICE_INTERFACE_DATA();
				dia.cbSize = Util.getStructSize(dia);

				// start the enumeration
				success = SetupDiEnumDeviceInterfaces(h, 0, DiskGUID, i, dia);
				if (success) {
					// build a DevInfo Data structure
					SP_DEVINFO_DATA da = new SP_DEVINFO_DATA();
					da.cbSize = Util.getStructSize(da);

					// build a Device Interface Detail Data structure
					SP_DEVICE_INTERFACE_DETAIL_DATA didd = new SP_DEVICE_INTERFACE_DETAIL_DATA();
					didd.cbSize = 4 + Util.sizeOfWideChar(); // trust me :)

					// now we can get some more detailed information
					int[] nRequiredSize = { 0 };

					SetupDiGetDeviceInterfaceDetail(h, dia, null, 0,
							nRequiredSize, null);

					if (SetupDiGetDeviceInterfaceDetail(h, dia, didd,
							nRequiredSize[0], nRequiredSize, da)) {
						int nBytes = 128;
						System.out.println("DevicePath = " + didd.DevicePath);

						// current InstanceID is at the "USBSTOR" level, so we
						// need up "move up" one level to get to the "USB" level
						int[] ptrPrevious = { 0 };
						CM_Get_Parent(ptrPrevious, da.DevInst, 0);
						
						// Now we get the InstanceID of the USB level device
						byte[] ba = new byte[nBytes];
						int ptrInstanceBuf = Util.byteArrayToPtr(ba);
						CM_Get_Device_ID(ptrPrevious[0], ptrInstanceBuf, nBytes, 0);
						String InstanceID = Util.ptrToString(ptrInstanceBuf);
						System.out.println("InstanceID = " + InstanceID + "\n");
					}
				}
				i++;
			}
		}
		SetupDiDestroyDeviceInfoList(h);
	}
}
