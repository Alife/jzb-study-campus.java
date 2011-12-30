package jinvoke.samples.win32;


// This class is used by the ChooseColorTest sample, and represents the CHOOSECOLOR stuct
import com.jinvoke.Callback;
import com.jinvoke.NativeStruct;

@NativeStruct
public class CHOOSECOLOR {
	  public int lStructSize;
	  public int hwndOwner;
	  public int hInstance;
	  public int rgbResult;
	  public int[] lpCustColors;
	  public int flags;
	  public int lCustData;
	  public Callback lpfnHook;
	  public String lpTemplateName;
}
