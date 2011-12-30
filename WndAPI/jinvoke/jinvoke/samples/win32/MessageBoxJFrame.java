package jinvoke.samples.win32;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.win32.WinConstants;

public class MessageBoxJFrame {
	
	@NativeImport(library="User32")
	public static native int MessageBox(int hwnd, String text, String caption, int type);
	
	public static void main(String[] args) {
		JInvoke.initialize();
		
		JFrame frame = new JFrame("J/Invoke Sample");
		frame.setBounds(300, 300, 300, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton b = new JButton("Call Win32 API native");
		b.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				MessageBox(0, "Win32 MessageBox", "Hello from J/Invoke",
						WinConstants.MB_ICONINFORMATION | WinConstants.MB_OK);
			}});
		frame.getContentPane().add(b);
		
		frame.setVisible(true);
	}
}
