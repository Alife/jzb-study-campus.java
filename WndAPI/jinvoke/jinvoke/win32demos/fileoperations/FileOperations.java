// FileOperations.java
// Gayathri Singh, March 2008, gayathri@byteblend.com

/* This class performs file operations such as copy, move or delete using
   the Shell32.SHFileOperation API. This enables it to use the animation dialogs, 
   as well as other settings easily.
   
   This class is designed to be used along with FileOperationsUI, which provides 
   a GUI frontend for this class.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.fileoperations.FileOperations
*/

package jinvoke.win32demos.fileoperations;
import com.jinvoke.win32.Shell32;
import com.jinvoke.win32.structs.*;
import javax.swing.UIManager;
import static com.jinvoke.win32.WinConstants.*;

public class FileOperations {

    FileOperationsUI fileOpsUI;
        
    private void setFileOperationsUI(FileOperationsUI fileOpsUI) {
        this.fileOpsUI = fileOpsUI;
    }
    
    public static void main(String[] args) throws Exception {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            java.awt.EventQueue.invokeLater(new Runnable() {

                public void run() {
                    try {
                        FileOperations fileOps = new FileOperations();
                        FileOperationsUI fileOpsUI = new FileOperationsUI(fileOps);
                        fileOps.setFileOperationsUI(fileOpsUI);
                        fileOpsUI.setLocationRelativeTo(null); //center the JFrame
                        fileOpsUI.setVisible(true);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }

               }
           });
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }

    }

    // performs the file operation using SHFileOperation
    public void performFileOperation(int selectedCommand) {

    	int fileOpCommand = 0;
    	int flags = 0;
    	
    	// set the file operation
        switch (selectedCommand) {
            case 0:
                fileOpCommand = FO_COPY;
                break;
            case 1:
                fileOpCommand = FO_MOVE;
                break;
            case 2:
                fileOpCommand = FO_RENAME;
                break;
        }
    	
        // set the flags based on the user interface selections
    	if (fileOpsUI.chkShowProgress.isSelected()==false)
    		flags = flags | FOF_SILENT;
    	if (fileOpsUI.chkYesToAll.isSelected()==false)
    		flags = flags | FOF_NOCONFIRMATION;    		
    	if (fileOpsUI.chkNameCollisionRename.isSelected())
    		flags = flags | FOF_RENAMEONCOLLISION;    		
    	if (fileOpsUI.chkConfirmDirCreation.isSelected())
    		flags = flags | FOF_NOCONFIRMMKDIR;    		
    	if (fileOpsUI.chkFilesOnly.isSelected())
    		flags = flags | FOF_FILESONLY;    		
    	
    	// by adding the FOF_ALLOWUNDO flag you can move
    	// a file to the Recycle Bin instead of deleting it
    	// we'll do that in the RecycleBin hack
    	

    	// create a ShFileOpStruct struct and set its members
    	ShFileOpStruct shFileOp = new ShFileOpStruct();
    	shFileOp.wFunc = fileOpCommand;
    	shFileOp.pFrom = fileOpsUI.txtFieldSource.getText()+ "\0";
    	shFileOp.pTo =  fileOpsUI.txtFieldDestination.getText() + "\0";
    	shFileOp.fFlags = (short) flags;
    	
    	// perform the requested file operation
        Shell32.SHFileOperation(shFileOp);        
    }
}
