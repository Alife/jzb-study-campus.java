package jinvoke.samples.win32;
import com.jinvoke.win32.User32;
import com.jinvoke.win32.Advapi32;;
public class GetUserName {
	  public static void main (String[] args)   {
		    int[] buffer =  { 128 } ;
		    StringBuffer username = new StringBuffer((int)buffer[0]);
		    Advapi32.GetUserName(username, buffer);
		    System.out.println("UserName: " + username);
		    User32.MessageBox(0, "UserName : " + username,  "J/Invoke MessageBox", 0);
	  }
}
