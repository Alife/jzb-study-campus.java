package jinvoke.win32demos.eventlog;

import static com.jinvoke.win32.WinConstants.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.jinvoke.CallingConvention;
import com.jinvoke.JInvoke;
import com.jinvoke.NativeImport;
import com.jinvoke.Util;
import com.jinvoke.win32.Advapi32;
import com.jinvoke.win32.Kernel32;
import com.jinvoke.win32.structs.EventLogRecord;

public class EventLogViewer extends JPanel {

	@NativeImport(library = "kernel32")
	public static native int FormatMessage(int dwFlags, int lpSource,
			int dwmessageID, int dwLanguageID, StringBuffer lpBuffer,
			int nSize, int[] arguments);

	@NativeImport(library = "kernel32", function = "FormatMessage")
	public static native int FormatMessageLong(int dwFlags, int lpSource,
			int dwmessageID, int dwLanguageID, int[] lpBuffer, int nSize,
			int[] arguments);

	@NativeImport(library = "msvcrt", convention = CallingConvention.CDECL)
	public static native int strlen(int ptrToString);

	@NativeImport(library = "msvcrt", convention = CallingConvention.CDECL)
	public static native int wcslen(int ptrToString);

	static JFrame frame;

	static final int ELF_LOGFILE_HEADER_DIRTY = 0x0001;
	static final int ELF_LOGFILE_HEADER_WRAP = 0x0002;
	static final int ELF_LOGFILE_LOGFULL_WRITTEN = 0x0004;
	static final int ELF_LOGFILE_ARCHIVE_SET = 0x0008;

	int[] pnBytesRead = { 0 };

	int bytesOffset = 0;

	int ptrToEventLogRec = 0;

	EventLogRecord eventLogRec = new EventLogRecord();

	byte[] buffer;

	String machineName;

	ArrayList<String> modulesToBeUnloaded = new ArrayList<String>();

	boolean debugStop = false;

	int debugCount = 0;

	public EventLogViewer() {
		super(new BorderLayout());

		JTextArea eventMessage = new JTextArea();
		eventMessage.setEditable(false);
		eventMessage.setLineWrap(true);
		eventMessage.setWrapStyleWord(true);
		
		JTabbedPane tabbedPane = new JTabbedPane();

		JComponent appPanel = makePanel("Application", eventMessage);
		appPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab("Application", appPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent sysPanel = makePanel("System", eventMessage);
		sysPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab("System", sysPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

		JComponent securityPanel = makePanel("Security", eventMessage);
		securityPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		tabbedPane.addTab("Security", securityPanel);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_3);


		JScrollPane messagePane = new JScrollPane(eventMessage);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(tabbedPane);
		splitPane.setBottomComponent(messagePane);

		Dimension minimumSize = new Dimension(500, 50);
		messagePane.setMinimumSize(minimumSize);
		tabbedPane.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(390);
		splitPane.setPreferredSize(new Dimension(500, 300));

		splitPane.setBorder(BorderFactory.createEmptyBorder());
		// Add the split pane to this panel.
		add(splitPane);

		JPanel buttonPanel = new JPanel();

		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton refreshBtn = new JButton("Refresh");
		refreshBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});

		buttonPanel.add(refreshBtn);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	protected JComponent makePanel(String logName, JTextArea eventMessage) {
		EventLogTableModel eventLogTableModel = new EventLogTableModel();
		JTable table = new JTable(eventLogTableModel);
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		//table.setFillsViewportHeight(true);
		table.setShowHorizontalLines(false);
		//table.setAutoCreateRowSorter(true);
		SelectionListener selectionListener = new SelectionListener(table,
				eventMessage);
		table.getSelectionModel().addListSelectionListener(selectionListener);

		((EventLogTableModel) table.getModel()).setData(readEventLog(logName));

		table.getColumnModel().getColumn(0).setPreferredWidth(90);
		table.getColumnModel().getColumn(1).setPreferredWidth(165);
		table.getColumnModel().getColumn(2).setPreferredWidth(50);
		table.getColumnModel().getColumn(3).setPreferredWidth(50);
		table.getColumnModel().getColumn(4).setPreferredWidth(90);
		table.getColumnModel().getColumn(5).setPreferredWidth(90);
		table.getColumnModel().getColumn(6).setPreferredWidth(90);
		table.getColumnModel().getColumn(7).setPreferredWidth(300);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Create the scroll pane and add the table to it.
		JScrollPane eventLogPane = new JScrollPane(table);
		// Add the scroll pane to this panel.
		add(eventLogPane);

		eventLogPane.getViewport().setBackground(Color.WHITE);

		JPanel panel = new JPanel(false);
		panel.setLayout(new BorderLayout());
		panel.add(eventLogPane, BorderLayout.CENTER);
		return panel;
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame("J/Invoke Event Log Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		EventLogViewer eventLogViewer = new EventLogViewer();
		eventLogViewer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Add content to the window.
		frame.add(eventLogViewer, BorderLayout.CENTER);

		// Display the window.
		frame.pack();
		frame.setBounds(300, 200, 750, 600);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		JInvoke.initialize();

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
				}
				createAndShowGUI();
			}
		});
	}

	public String[][] readEventLog(String logName) {

		String[][] eventLogArray = new String[200][8];
		int recordNum = 0;
		machineName = System.getenv("COMPUTERNAME");
		int hEventLog;

		hEventLog = Advapi32.OpenEventLog(machineName, logName);

		if (hEventLog == INVALID_HANDLE_VALUE)
			return eventLogArray;

		// Get number of records
		int recordCount[] = { 0 };
		Advapi32.GetNumberOfEventLogRecords(hEventLog, recordCount);

		if (recordCount[0] > 200)
			recordCount[0] = 200; // Only getting the first 200 records

		// Get first record
		int[] firstRecord = { 0 };
		Advapi32.GetOldestEventLogRecord(hEventLog, firstRecord);

		for (int i = firstRecord[0]; i < firstRecord[0] + recordCount[0]; i++) {

			if ((bytesOffset + eventLogRec.Length) < pnBytesRead[0]) {
				eventLogArray[recordNum] = getBufferedRecord(logName);
			} else {
				eventLogArray[recordNum] = getNextRecord(hEventLog,
						EVENTLOG_FORWARDS_READ | EVENTLOG_SEEK_READ, i, logName);
			}
			recordNum++;
			// if (debugStop == true) break;
		}
		return eventLogArray;
	}

	public String[] getNextRecord(int hEventLog, int flags, int recordNum,
			String logName) {
		String[] bufferedRecordData = new String[8];
		// Get number of bytes needed
		pnBytesRead = new int[1];
		pnBytesRead[0] = 0;
		bytesOffset = 0;
		buffer = new byte[1];
		int nNumberOfBytesToRead = 0;
		int[] pnMinNumberOfBytesNeeded = { 0 };
		ptrToEventLogRec = 0;
		Advapi32.ReadEventLog(hEventLog, flags, recordNum, buffer,
				nNumberOfBytesToRead, pnBytesRead, pnMinNumberOfBytesNeeded);

		buffer = new byte[pnMinNumberOfBytesNeeded[0]];
		nNumberOfBytesToRead = pnMinNumberOfBytesNeeded[0];

		Advapi32.ReadEventLog(hEventLog, flags, recordNum, buffer,
				nNumberOfBytesToRead, pnBytesRead, pnMinNumberOfBytesNeeded);

		bufferedRecordData = getBufferedRecord(logName);
		return bufferedRecordData;
	}

	private String[] getBufferedRecord(String logName) {
		String[] bufferedRecordData = new String[8];
		// reads the next buffered EventLog record from our local buffer.
		if (ptrToEventLogRec != 0) {
			bytesOffset += eventLogRec.Length;
			ptrToEventLogRec = Util.byteArrayToPtr(buffer) + bytesOffset;
		} else {
			ptrToEventLogRec = Util.byteArrayToPtr(buffer);
		}
		eventLogRec = Util.ptrToStruct(ptrToEventLogRec, EventLogRecord.class);

		String eventType;
		switch (eventLogRec.EventType) {
		case EVENTLOG_AUDIT_FAILURE:
			eventType = "Audit Failure";
			break;
		case EVENTLOG_AUDIT_SUCCESS:
			eventType = "Audit Success";
			break;
		case EVENTLOG_ERROR_TYPE:
			eventType = "Error";
			break;
		case EVENTLOG_INFORMATION_TYPE:
			eventType = "Information";
			break;
		case EVENTLOG_SUCCESS:
			eventType = "Success";
			break;
		case EVENTLOG_WARNING_TYPE:
			eventType = "Warning";
			break;
		default:
			eventType = "Unknown";
			break;
		}
		bufferedRecordData[0] = eventType;

		if (eventLogRec.TimeGenerated != 0) {
			Calendar c1 = Calendar.getInstance();
			c1.set(1970, 0, 1);
			c1.add(Calendar.SECOND, eventLogRec.TimeGenerated);
			Date localTime = new Date(c1.getTimeInMillis());
			SimpleDateFormat sd = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			bufferedRecordData[1] = sd.format(localTime);
		} else {
			bufferedRecordData[1] = "";
		}

		int eventID = loword(eventLogRec.EventID);
		bufferedRecordData[2] = Integer.toString(eventID);

		String userID = getUserID(eventLogRec.UserSidLength,
				eventLogRec.UserSidOffset);
		bufferedRecordData[3] = userID;

		String source = Util.ptrToString(ptrToEventLogRec
				+ Util.getStructSize(eventLogRec));
		bufferedRecordData[4] = source;

		String machineName = Util.ptrToString(ptrToEventLogRec
				+ Util.getStructSize(eventLogRec) + source.length()
				* Util.sizeOfWideChar() + 2);
		bufferedRecordData[5] = machineName;

		int moduleHandle;
		moduleHandle = loadMessageLibrary(logName, source);
		String eventCategory = "";
		if (eventLogRec.EventCategory != 0) {
			int[] lpEventString = { 0 };
			int[] arguments = { 0 };
			int flags = FORMAT_MESSAGE_FROM_HMODULE
					| FORMAT_MESSAGE_IGNORE_INSERTS
					| FORMAT_MESSAGE_ALLOCATE_BUFFER;
			FormatMessageLong(flags, moduleHandle,
					eventLogRec.EventCategory, 0, lpEventString, 0, arguments);
			eventCategory = Util.ptrToString(lpEventString[0]);
			if (eventCategory == null) {
				eventCategory = "None";
			}
		} else {
			eventCategory = "None";
		}
		if (eventCategory.trim().equals("")) {
			eventCategory = "None";
		}
		bufferedRecordData[6] = eventCategory;

		String message = getMessage(moduleHandle, eventLogRec);
		bufferedRecordData[7] = message;

		return bufferedRecordData;

	}

	private String getUserID(int userIDLength, int userIDOffset) {
		if (userIDOffset < 0) {
			return "";
		}
		StringBuffer userID = new StringBuffer(64);
		StringBuffer lpReferencedDomainName = new StringBuffer(64);
		int[] peUse = { 0 };
		int[] cchName = new int[1];
		cchName[0] = userID.capacity();
		int[] cchReferencedDomainName = new int[1];
		cchReferencedDomainName[0] = lpReferencedDomainName.capacity();
		Advapi32.LookupAccountSid(null, ptrToEventLogRec + userIDOffset,
				userID, cchName, lpReferencedDomainName,
				cchReferencedDomainName, peUse);
		if (userID.toString().trim().equals(""))
			return "N/A";
		else
			return userID.toString().trim();
	}

	private String getMessage(int moduleHandle, EventLogRecord eventLogRec) {
		int numChars = 0;
		String message = "";
		int[] lpEventString = { 0 };
		int[] arguments = { 0 };
		int flags;

		if (moduleHandle > 0) {
			flags = FORMAT_MESSAGE_FROM_HMODULE | FORMAT_MESSAGE_IGNORE_INSERTS
					| FORMAT_MESSAGE_ALLOCATE_BUFFER;
			numChars = FormatMessageLong(flags, moduleHandle,
					eventLogRec.EventID, 0, lpEventString, 0, arguments);
		}

		int capacity = (eventLogRec.DataOffset - eventLogRec.StringOffset);
		ArrayList<Integer> args = new ArrayList<Integer>();
		int startPtr = ptrToEventLogRec + eventLogRec.StringOffset;
		int strLength;
		int totalLengthSoFar = 0;
		String sArgString = "";
		while (totalLengthSoFar < capacity) {
			args.add(startPtr);
			strLength = wcslen(startPtr);
			sArgString += Util.ptrToString(startPtr) + " ";
			startPtr += (strLength * 2) + 2;
			totalLengthSoFar += (strLength * 2) + 2;
		}

		if (numChars == 0) {
			if (sArgString != "") {
				message += "\n" + sArgString;
			} else {
				message = "None";
			}
			return message;
		} else {

			flags = FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_STRING
					| FORMAT_MESSAGE_ARGUMENT_ARRAY;

			int[] mesg = { 0 };
			int idx = 0;
			int[] argsForFmting = new int[args.size()];
			for (int v : args) {
				argsForFmting[idx++] = v;
			}
			numChars = FormatMessageLong(flags, lpEventString[0], 0, 0, mesg,
					numChars + capacity, argsForFmting);

			return Util.ptrToString(mesg[0]);
		}
	}

	private int loadMessageLibrary(String logName, String sourceName) {
		String modulePath;
		int hKey[] = { 0 };
		Advapi32.RegOpenKeyEx(HKEY_LOCAL_MACHINE,
				"SYSTEM\\CurrentControlSet\\Services\\EventLog\\" + logName
						+ "\\" + sourceName, 0, KEY_QUERY_VALUE, hKey);
		int[] lpType = { 0 };
		byte[] lpData = new byte[1];
		int[] lpcbData = new int[1];
		lpcbData[0] = 1;
		String valueKey = "EventMessageFile";
		Advapi32.RegQueryValueEx(hKey[0], valueKey, null, lpType,
				lpData, lpcbData);

		StringBuffer dataStr = new StringBuffer(lpcbData[0]);
		Advapi32.RegQueryValueExStr(hKey[0], valueKey, null, lpType,
				dataStr, lpcbData);
		modulePath = dataStr.toString();

		StringBuffer lpDst = new StringBuffer(64);
		if (modulePath.contains("%")) {

			Kernel32.ExpandEnvironmentStrings(modulePath, lpDst, lpDst
					.capacity() + 1);

		}
		modulePath = lpDst.toString().trim();

		int handle = Kernel32.LoadLibraryEx(modulePath, 0,
				DONT_RESOLVE_DLL_REFERENCES);

		modulesToBeUnloaded.add(modulePath);

		return handle;

	}

	private int loword(int i) {
		return i & 0x0000ffff;
	}

}

class EventLogTableModel extends AbstractTableModel {
	private String[] columnNames = { "Type", "Date", "ID", "User", "Source",
			"Computer", "Category", "Message" };

	private Object[][] data = { { "", "", "", "", "", "", "", "" } };

	public String getColumnName(int i) {
		return columnNames[i];
	}

	public void setData(Object[][] data) {
		this.data = data;
		fireTableDataChanged();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return data.length;
	}

	public Object getValueAt(int row, int col) {
		return data[row][col];

	}

}

class SelectionListener implements ListSelectionListener {
	JTable table;

	JTextArea eventMessage;

	// It is necessary to keep the table since it is not possible
	// to determine the table from the event's source
	SelectionListener(JTable table, JTextArea eventMessage) {
		this.table = table;
		this.eventMessage = eventMessage;
	}

	public void valueChanged(ListSelectionEvent e) {
		// If cell selection is enabled, both row and column change events are
		// fired
		if (e.getSource() == table.getSelectionModel()
				&& table.getRowSelectionAllowed()) {
			if (table.getSelectedRow() != -1) {
				eventMessage.setText("Selected Event Message: \n"
						+ (table.getValueAt(table.getSelectedRow(), 7)
								.toString()));
			}
		}

		if (e.getValueIsAdjusting()) {
			// The mouse button has not yet been released
		}
	}
}
