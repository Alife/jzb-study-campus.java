// RecycleBin.java
// Gayathri Singh, March 2008, gayathri@byteblend.com
// TODO : needs refactoring

/* This class deletes files by moving them to the Recycle Bin file. It also 
   provides methods for querying the size of the recycle bin and emptying the bin.
   
   This class is designed to be used along with RecycleBinUI, which provides 
   a GUI frontend for this class.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.recyclebin.RecycleBin
*/
package jinvoke.win32demos.recyclebin;
import static com.jinvoke.win32.WinConstants.FOF_ALLOWUNDO;
import static com.jinvoke.win32.WinConstants.FO_DELETE;
import static com.jinvoke.win32.WinConstants.MOVEFILE_DELAY_UNTIL_REBOOT;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jinvoke.JInvoke;
import com.jinvoke.Util;
import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.Shell32;
import com.jinvoke.win32.structs.ShFileOpStruct;
import com.jinvoke.win32.structs.ShQueryRBInfo;


public class RecycleBin {
    
    private static final int SHERB_NOCONFIRMATION = 1;
    private static final int SHERB_NOPROGRESSUI = 2;
    private static final int SHERB_NOSOUND = 4;

    RecycleBinUI recycleBinUI;
    
    private void setRecycleBinUI(RecycleBinUI binUI) {
        this.recycleBinUI = binUI;
    }
    
    public static void main(String[] args) throws Exception {
        JInvoke.initialize();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    try {
                        RecycleBin recycleBin = new RecycleBin();
                        RecycleBinUI recycleBinUI = new RecycleBinUI(recycleBin);
                        recycleBin.setRecycleBinUI(recycleBinUI);
                        recycleBinUI.setLocationRelativeTo(null); //center the JFrame

                        recycleBin.queryRecycleBin();
                        recycleBinUI.setVisible(true);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }

               }
           });
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }
    
    // empties the contents of the Recycle Bin
    public void emptyRecycleBin(StringBuffer binDrive) {
        int flags =0;
        if (binDrive.toString().equals(""))
                binDrive=null;
        if (recycleBinUI.chkNoConfirmationDialog.isSelected())
            flags = SHERB_NOCONFIRMATION;
        if (recycleBinUI.chkNoProgressDialog.isSelected())
            flags = flags |SHERB_NOPROGRESSUI;
        if (recycleBinUI.chkNoSound.isSelected())
            flags = flags |SHERB_NOSOUND;

        Shell32.SHEmptyRecycleBin(Util.getWindowHandle(recycleBinUI), binDrive, flags);
    }

    // queries the number of files in the Recycle Bin
    public String queryRecycleBin() {
        ShQueryRBInfo shQBInfo = new ShQueryRBInfo();
           
        shQBInfo.cbSize = Util.getStructSize(ShQueryRBInfo.class);
        
        Shell32.SHQueryRecycleBin("C:\\", shQBInfo);
 
        String result =  "";
        if (shQBInfo.i64NumItems !=0)
            result = "The Recycle Bin has " + shQBInfo.i64NumItems + " items";
        else
            result = "The Recycle Bin is empty";
        recycleBinUI.lblRecycleBin.setText(result);
        return result;
    }
    
    // deteles files by moving them or the recycle bin
    // or by marking them for deletion when the system reboots
    public void deleteFiles(int selectedCommand) {

    	int flags = 0;
    	ShFileOpStruct shFileOp = new ShFileOpStruct();
    	
        if (recycleBinUI.txtFieldSource.getText().equals(""))
            return;
        
        File f = new File(recycleBinUI.txtFieldSource.getText());
        
        if ((selectedCommand == 0) || (selectedCommand == 1)) {
            if (selectedCommand == 0) {
                flags = FOF_ALLOWUNDO;
            }

            // by adding the FOF_ALLOWUNDO flag you can move
            // a file to the Recycle Bin instead of deleting it

            shFileOp.wFunc = FO_DELETE;
            shFileOp.pFrom = recycleBinUI.txtFieldSource.getText()+ "\0";
            shFileOp.fFlags = (short)flags;
            
            // delete the file using the options chosen
            int result = Shell32.SHFileOperation(shFileOp);  
                
            if (result == 0)
                JOptionPane.showMessageDialog(null, f.getName() + " deleted successfully", "Selection Deleted", JOptionPane.INFORMATION_MESSAGE);
            else 
            	System.out.println(Util.getLastError());

        }
        else if (selectedCommand == 2) { 
        	// mark the file for deletion. The file is deleted when the system reboots.
            boolean retVal = Kernel32.MoveFileEx(recycleBinUI.txtFieldSource.getText(), null, MOVEFILE_DELAY_UNTIL_REBOOT);
            if (retVal==true)
            	JOptionPane.showMessageDialog(null,  f.getName() + " has been marked for deletion when the system reboots", "Selection Marked for Deletion", JOptionPane.INFORMATION_MESSAGE);
        }
    }


}


