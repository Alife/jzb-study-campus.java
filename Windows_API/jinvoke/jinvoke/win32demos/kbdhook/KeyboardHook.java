package jinvoke.win32demos.kbdhook;
import static com.jinvoke.win32.WinConstants.*;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jinvoke.Callback;
import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.Util;
import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.User32;
import com.jinvoke.win32.structs.KBDllHookStruct;
import com.jinvoke.win32.structs.Msg;

public class KeyboardHook extends JPanel{
	static {
		JInvoke.initialize();
	}	
	
	@NativeImport(library = "user32")
	public native static int SetWindowsHookEx (int idHook, Callback hookProc, int hModule, int dwThreadId);
	
	@NativeImport(library = "user32")
	public native static int UnhookWindowsHookEx (int idHook);
	
	public static final int WH_KEYBOARD_LL = 13;
	static JFrame frame;
	
	static TextArea keyboardEventArea = new TextArea();
	static JButton setHookBtn;
	static JButton removeHookBtn;
	
	public KeyboardHook() {
        super(new BorderLayout());

		keyboardEventArea.setText("1) Click the \"Set Keyboard Hook\" button.\n" +
				"2) Start typing anywhere on the desktop.  Keys pressed will be captured here.\n" +
				"3) Stop the keyboard hook by clicking the \"Remove Keyboard Hook\" button.\n\n");
		
	    JScrollPane keyboardEventPane = new JScrollPane(keyboardEventArea);
	    
        add(keyboardEventPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        setHookBtn = new JButton("Set Keyboard Hook");
        setHookBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setKeyboardHook();
			}} );
        
        removeHookBtn = new JButton("Remove Keyboard Hook");
        removeHookBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				unsetKeyboardHook();
			}} );
        removeHookBtn.setEnabled(false);
        buttonPanel.add(setHookBtn);	        
        buttonPanel.add(removeHookBtn);	     
        add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void setKeyboardHook() {
		setHookBtn.setEnabled(false);
		removeHookBtn.setEnabled(true);

		// This hook is called in the context of the thread that installed it. 
		// The call is made by sending a message to the thread that installed the hook.
		// Therefore, the thread that installed the hook must have a message loop. 
		//
		// We crate a new thread as we don't want the AWT Event thread to be stuck running a message pump
		// nor do we want the main thread to be stuck in running a message pump
		Thread hookThread = new Thread(new Runnable(){

			public void run() {
				if (KeyBoardProc.hookHandle == 0) {
					int hInstance = Kernel32.GetModuleHandle(null);
							
					KeyBoardProc.hookHandle = SetWindowsHookEx(WH_KEYBOARD_LL, 
									new Callback(KeyBoardProc.class, "lowLevelKeyboardProc"), 
									hInstance, 
									0);
					
					
					// Standard message dispatch loop
					Msg msg = new Msg();
					while (User32.GetMessage(msg, 0, 0, 0)) {
						User32.TranslateMessage(msg);
						User32.DispatchMessage(msg);
					}
					
				} else {
					keyboardEventArea.append("The Hook is already installed.\n");
				}
			}});
		hookThread.start();
 	}

	private void unsetKeyboardHook() {
		setHookBtn.setEnabled(true);
		removeHookBtn.setEnabled(false);
		UnhookWindowsHookEx(KeyBoardProc.hookHandle);
		KeyBoardProc.hookHandle = 0;
	}
	
	
	private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Keyboard Hook");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        KeyboardHook keyboardEventsWindow = new KeyboardHook();
        keyboardEventsWindow.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        //Add content to the window.
        frame.add(keyboardEventsWindow, BorderLayout.CENTER);
   
        //Display the window.
        frame.pack();
         
        frame.setBounds(300, 200, 750, 600);
        frame.setVisible(true);
    }
	
	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

	}
}
		
class KeyBoardProc {
	static int shiftCount =0 ;
	static int hookHandle;
	
	@NativeImport(library = "user32")
	public native static int CallNextHookEx (int idHook, int nCode, int wParam, int lParam);
	static {
		JInvoke.initialize();
	}	
	
	public static int lowLevelKeyboardProc(int nCode, int wParam, int lParam ) {
		if (nCode < 0)
			return CallNextHookEx(hookHandle, nCode, wParam, lParam);
		KBDllHookStruct kbdllhs;
		boolean shiftPressed = false;
		if (nCode == HC_ACTION) {
			kbdllhs = Util.ptrToStruct(lParam, KBDllHookStruct.class);
			
			String message = getKeyValue(kbdllhs.vkCode, shiftPressed);
			if (wParam == WM_KEYDOWN)
				message += " pressed.\n";
			else if (wParam == WM_KEYUP)
				message += " released.\n";
					
			System.out.println(message); 

			KeyboardHook.keyboardEventArea.append(message);
		}
		return CallNextHookEx(hookHandle, nCode, wParam, lParam);
	}
	
	private static String getKeyValue(int vkCode, boolean shiftPressed) {
	    switch (vkCode) {    
	    case VK_SHIFT: if (shiftCount == 0) {shiftCount++; return "10     [SHIFT]";}
	    case VK_RSHIFT: return "0xA1   [RSHIFT]";
	    case VK_BACK: return "8       [BKSPACE]";
	    case VK_TAB: return "9       [TAB]";
	    case VK_RETURN: return "0D      [ENTER]";
	    case VK_CONTROL: return "11     [CTRL]";
	    case VK_MENU: return "12     [ALT]" ;
	    case VK_PAUSE: return "13     [PAUSE]";
	    case VK_CAPITAL: return "14     [Caps Lock]";
	    case VK_ESCAPE: return "1B     [ESC]";
	    case VK_SPACE: return "20     [SPACE]";
	    case VK_PRIOR: return "21     [PGUP]";
	    case VK_NEXT: return "22     [PGDN]";
	    case VK_END: return "23     [END]";
	    case VK_HOME: return "24     [HOME]";
	    case VK_LEFT: return "25     [LEFT]";
	    case VK_UP: return "26     [UP]";
	    case VK_RIGHT: return "27     [RIGHT]";
	    case VK_DOWN: return "28     [DOWN]";
	    case VK_INSERT: return "2D     [INS]";
	    case VK_DELETE: return "2E     [DEL]";
	    case VK_LWIN: return "5B     [LEFT WIN]";
	    case VK_RWIN: return "5C     [RIGHT WIN]";
	    case VK_APPS: return "5D     [A short cut]";
	    //If NumLock is on:
	    case VK_NUMPAD0: return "60     Num Pad 0";
	    case VK_NUMPAD1: return "61     Num Pad 1";
	    case VK_NUMPAD2: return "62     Num Pad 2";
	    case VK_NUMPAD3: return "63     Num Pad 3";
	    case VK_NUMPAD4: return "64     Num Pad 4";
	    case VK_NUMPAD5: return "65     Num Pad 5";
	    case VK_NUMPAD6: return "66     Num Pad 6";
	    case VK_NUMPAD7: return "67     Num Pad 7";
	    case VK_NUMPAD8: return "68     Num Pad 8";
	    case VK_NUMPAD9: return "69     Num Pad 9";
	    case VK_MULTIPLY: return "6A     Num Pad *";
	    case VK_ADD: return "6B     Num Pad +";
	    case VK_SUBTRACT: return "6D     Num Pad -";
	    case VK_DECIMAL: return "6E     Num Pad .";
	    case VK_DIVIDE: return "6F     Num Pad /";
	    case VK_F1: return "70     [F1]";
	    case VK_F2: return "71     [F2]";
	    case VK_F3: return "72     [F3]";
	    case VK_F4: return "73     [F4]";
	    case VK_F5: return "74     [F5]";
	    case VK_F6: return "75     [F6]";
	    case VK_F7: return "76     [F7]";
	    case VK_F8: return "77     [F8]";
	    case VK_F9: return "78     [F9]";
	    case VK_F10: return "79     [F10]";
	    case VK_F11: return "7A     [F11]";
	    case VK_F12: return "7B     [F12]";
	    case VK_NUMLOCK: return "90     [Num Lock]";
	    case VK_SCROLL: return "91     [Scroll Lock]";

	    default:
	    	if ((vkCode >= 33) && (vkCode <= 256)) {
		    	shiftCount =0;
	            //For A-Z
                if ((vkCode >= 65) && (vkCode <= 90)) {
                    if (shiftPressed) {
                         return  Integer.toHexString(vkCode) + "     " + (char) vkCode;
                    } else {
                         return Integer.toHexString(vkCode + 32) + "(ASCII)" + "     " + (char)(vkCode + 32);
                    }
                } else if ((vkCode >= 48) && (vkCode <= 57)) {
                	if (shiftPressed) {
                         switch (vkCode) {
                             case 48: return Integer.toHexString(41) + "(ASCII)" + "     " + ")";
                             case 49: return Integer.toHexString(33) + "(ASCII)" + "     " + "!";
                             case 50: return Integer.toHexString(64) + "(ASCII)" + "     " + "@";
                             case 51: return Integer.toHexString(35) + "(ASCII)" + "     " + "#";
                             case 52: return Integer.toHexString(36) + "(ASCII)" + "     " + "$";
                             case 53: return Integer.toHexString(37) + "(ASCII)" + "     " + "%";
                             case 54: return Integer.toHexString(94) + "(ASCII)" + "     " + "^";
                             case 55: return Integer.toHexString(38) + "(ASCII)" + "     " + "&";
                             case 56: return Integer.toHexString(42) + "(ASCII)" + "     " + "*";
                             case 57: return Integer.toHexString(40) + "(ASCII)" + "     " + "(";
                         	}
                	} else {
                         return Integer.toHexString(vkCode) + "     " + (char)vkCode;
                	}
                } else if  (vkCode == 0xBA) {
                     if (shiftPressed) 
                          return "BA     :";
                     else 
                         return Integer.toHexString(59) + "(ASCII)" + "     " + ";" ;
                } else if  (vkCode == 0xBB) {
                    if (shiftPressed) 
                        return "BB     +";
                    else
                         return Integer.toHexString(43) + "(ASCII)" + "     " + "=";
                } else if (vkCode == 0xBC) {
                	if (shiftPressed) 
                         return "BC     <";
                    else
                         return Integer.toHexString(44) + "(ASCII)" + "     " + ",";
                } else if (vkCode == 0xBD) {
                      //0-9 (opposite)
                	if (shiftPressed) 
                         return "BD     _";
                    else
                         return Integer.toHexString(95) + "(ASCII)" + "     " + "-" ;
                } else if (vkCode == 0xBE) {
                	if (shiftPressed) 
                         return "BE     >";
                    else 
                         return Integer.toHexString(46) + "(ASCII)" + "     " + ".";
                } else if (vkCode == 0xBF) {
                	if (shiftPressed) 
                         return "BF     ?";
                	else
                         return Integer.toHexString(47) + "(ASCII)" + "     " + "/";
                } else if (vkCode == 0xC0) {
                	if (shiftPressed) 
                         return "C0     ~";
                    else
                         return Integer.toHexString(60) + "(ASCII)" + "     " + "`";

                } else if (vkCode == 0xDB) {
                	if (shiftPressed) 
                         return "DB     {";
                    else
                         return Integer.toHexString(91) + "(ASCII)" + "     " + "[";
                } else if  (vkCode == 0xDC) {
                	if (shiftPressed) 
                         return "DC     |";
                    else
                         return Integer.toHexString(92) + "(ASCII)" + "     " + "\\";
                } else if (vkCode == 0xDD) {
            		if (shiftPressed) 
                         return "DD     }";
                    else
                         return Integer.toHexString(93) + "(ASCII)" + "     " + "]";
                } else if (vkCode == 0xDE) {
            		if (shiftPressed) 
                         return "DE     " + "\"\"";
                    else
                         return Integer.toHexString(39) + "(ASCII)" + "     " + "'";
                } else if (vkCode == 0xFF) {
            		if (shiftPressed) 
                         return "FF     [Fn]";
                    else
                         return "[Menu]";
                } else  { //We don't know what it will be specifically
        	    	return Integer.toHexString(vkCode) + "     " + (char)vkCode;
	    		} 
	    	}  
	    	
	    	return Integer.toHexString(vkCode) + "     " + (char)vkCode;
	    }
	}
}

