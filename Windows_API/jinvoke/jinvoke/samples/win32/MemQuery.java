package jinvoke.samples.win32;

import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.structs.MemoryStatus;

public class MemQuery {
	public static void main(String[] args) {
		MemoryStatus ms = new MemoryStatus();
		Kernel32.GlobalMemoryStatus(ms);
		System.out.printf("RAM\n Total Ram Size : %.0f MB\n Free : %.0f MB\n",ms.dwTotalPhys/(1048576.0),ms.dwAvailPhys/(1048576.0));
		System.out.printf("Used Memory :%.0f MB",ms.dwTotalPhys/(1048576.0) - ms.dwAvailPhys/(1048576.0));
	}
}
