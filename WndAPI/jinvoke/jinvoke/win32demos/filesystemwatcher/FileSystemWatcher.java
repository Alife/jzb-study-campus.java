// FileSystemWatcher.java
// Gayathri Singh, March 2008, gayathri@byteblend.com

/* This class listens to file system events using ReadDirectoryChangesW API from Kernel32.dll
   
   This class is designed to be used along with FileSystemWatcherUI, or some other class that
   provides it a JTextArea to provide output.
   
   The FileSystemWatcherUI class provides the GUI frontend for this class.
   
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.filesystemwatcher.FileSystemWatcherUI
*/
package jinvoke.win32demos.filesystemwatcher;

import com.jinvoke.win32.Kernel32;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import static com.jinvoke.win32.WinConstants.*;

public class FileSystemWatcher {
	
    // parameters set by user of this class
	String folder;
    int flags;
    boolean watchSubtree;
    JTextArea output;
    
    // parameters used internally to start and stop the file system watcher thread
    transient boolean stop = false;
    Thread watchThread;
    
    // constructor - sets the folder to watch, events to watch for, and provides 
    // JTextArea for output
    public FileSystemWatcher(String folder, int flags, boolean watchSubtree, JTextArea output) {
        this.folder = folder;
        this.flags = flags;
        this.watchSubtree = watchSubtree;
        this.output = output;
    }

    // watches the specified directory for file system events
    // this method is run in a separate thread created by the startWattching method
    public void watchFolder(String directory, int flags) {
    	
    	// open the directory to obtain a directory handle used by ReadDirectoryChangesW
        int dirHandle = Kernel32.CreateFile(directory, FILE_LIST_DIRECTORY,
                FILE_SHARE_READ + FILE_SHARE_DELETE + FILE_SHARE_WRITE,
                null,
                OPEN_EXISTING, FILE_FLAG_BACKUP_SEMANTICS, 0);

        // some basic error handling for sanity
        if (dirHandle == 0) {
            System.out.println("CreateFile Failed");
            return;
        }

        // create a buffer of 8K to read multiple file change events 
        // that occurred in between successive calls to ReadDirectoryChangesW function
        int BUFSIZE = 8 * 1048;
        byte[] buf = new byte[BUFSIZE];
        int[] bytesReturned = new int[1];

        stop = false;

        // call ReadDirectoryChangesW in a loop, till the 'Stop' button is pressed
        while (!stop) {
        	
            if (Kernel32.ReadDirectoryChangesW(dirHandle, buf, BUFSIZE, watchSubtree,
                    flags, bytesReturned, null, null)) {
            
            	if (bytesReturned[0] != 0) {
            		// wrap the byte array buffer in Java’s ByteBuffer class, 
            		// and set it’s endianness to LITTLE_ENDIAN - the native encoding in Win32
            		ByteBuffer bb = ByteBuffer.wrap(buf);
                    bb.order(ByteOrder.LITTLE_ENDIAN);

                    while (true) {
                        int prevEntryOffset = bb.position();
                        
                        // read FILE_NOTIFY_INFORMATION members - NextEntryOffset, Action & FileNameLength
                        int nextEntryOffset = bb.getInt();
                        final int action = bb.getInt();
                        int fileLen = bb.getInt();

                        // allocate a byte array long enough to hold the filename
                        byte[] stringbytes = new byte[fileLen];
                        // read filename into the byte-array  
                        bb.get(stringbytes);

                        try {
                        	// convert byte-array to Java String - the encoding Win32 uses is UTF-16LE
                            final String filename = new String(stringbytes, "UTF-16LE");

                            // fire the file event action
                            fileAction(action, new String(filename));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        if (stop || nextEntryOffset == 0) {
                            // no more file change events to be reported in this iteration
                        	// let's break out of the loop to call ReadDirectoryChangesW again
                        	break;
                        } else {
                        	// Skip nextEntryOffset from prevEntryOffset to read next file change event entry 
                            int newposition = prevEntryOffset + nextEntryOffset;
                            bb.position(newposition);
                        }
                    }
                }
            }
        }
    }

    // logs file system events to the provides JTextArea
    private void fileAction(final int action, final String filename) {
        // We update the state of Swing components here
        // As this method is called on the watcherThread, it is not safe
        // to update the output JTextPane component directly. We should use 
        // SwingUtilities.invokeLater to cause the component
        // to be updated from the AWT Event Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                switch (action) {
                    case FILE_ACTION_ADDED:
                        output.append("\nFile Added: ");
                        break;

                    case FILE_ACTION_MODIFIED:
                        output.append("\nFile Modified: ");
                        break;

                    case FILE_ACTION_REMOVED:
                        output.append("\nFile Modified: ");
                        break;

                    case FILE_ACTION_RENAMED_NEW_NAME:
                        output.append("\nFile Renamed - New Name: ");
                        break;

                    case FILE_ACTION_RENAMED_OLD_NAME:
                        output.append("\nFile Renamed - Old Name: ");
                        break;

                    default:
                }
                output.append(filename);
            }
        });
    }
    
    // starts a new thread that monitors the file system for file change events
    public void startWatching() {
        Runnable r = new Runnable() {
            public void run() {
                watchFolder(folder, flags);
            }
        };
        watchThread = new Thread(r);
        watchThread.start();
    }

    // sets a flag to stop the file system watcher thread
    public void stopWatching() {
         stop = true;
    }
}
