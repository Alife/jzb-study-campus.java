package jinvoke.samples.win32;
import com.jinvoke.win32.Shell32;
import com.jinvoke.win32.WinConstants;
import com.jinvoke.win32.structs.BrowseInfo;

public class BrowseForFolder {
	public static void main(String args[])
	{
		BrowseInfo bi = new BrowseInfo();
		String message ="Choose the file...";
	    if (message != null) 
	    	bi.lpszTitle = message;
	    
	    bi.pszDisplayName = new StringBuffer(1024);
	    bi.ulFlags = WinConstants.BIF_VALIDATE | WinConstants.BIF_RETURNONLYFSDIRS;
	    int pidl = Shell32.SHBrowseForFolder(bi);
	    if (pidl <= 0) {
	    	System.out.println("");
	    } else {
	      StringBuffer buf = new StringBuffer(WinConstants.BUFSIZ);
	      if (Shell32.SHGetPathFromIDList(pidl, buf)) {
	    	  System.out.println("buf="+buf.toString());
	    	  System.out.println("Display name:"+bi.pszDisplayName);
	      }
	    }
	}

}
