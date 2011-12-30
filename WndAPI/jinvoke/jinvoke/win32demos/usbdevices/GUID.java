package jinvoke.win32demos.usbdevices;

import com.jinvoke.Embedded;
import com.jinvoke.NativeStruct;

//typedef struct _GUID {
//      ULONG  Data1;
//      USHORT  Data2;
//      USHORT  Data3;
//      UCHAR  Data4[8];
//    } GUID

@NativeStruct
public class GUID{
    public int Data1;
    public short Data2;
    public short Data3;
    
    @Embedded(length=8)
    public byte[] Data4 = new byte[8]; 
}
