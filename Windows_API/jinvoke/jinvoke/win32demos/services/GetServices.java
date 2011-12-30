package jinvoke.win32demos.services;

import static com.jinvoke.win32.WinConstants.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.jinvoke.Util;
import com.jinvoke.win32.Advapi32;
import com.jinvoke.win32.structs.EnumServiceStatus;
import com.jinvoke.win32.structs.QueryServiceConfig;
import com.jinvoke.win32.structs.ServiceStatus;

public class GetServices extends JPanel {

	JFrame frame;

	String servicesArray[][];

	int servicesCount = 0;

	JTable servicesTable;

	public GetServices() {
		super(new BorderLayout());

		ServicesTableModel servicesModel = new ServicesTableModel();
		servicesTable = new JTable(servicesModel);
		servicesTable
				.setPreferredScrollableViewportSize(new Dimension(500, 70));
		//		servicesTable.setFillsViewportHeight(true);
		servicesTable.setShowHorizontalLines(false);
		//		servicesTable.setAutoCreateRowSorter(true);
		((ServicesTableModel) servicesTable.getModel())
				.setData(EnumerateServices());
		JScrollPane servicesPane = new JScrollPane(servicesTable);

		add(servicesPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton updateBtn = new JButton("Update");
		updateBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((ServicesTableModel) servicesTable.getModel())
						.setData(EnumerateServices());
			}
		});

		buttonPanel.add(updateBtn);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	private void createAndShowGUI() {
		//Create and set up the window.
		frame = new JFrame("Windows Services");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GetServices servicesWindow = new GetServices();
		servicesWindow.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//Add content to the window.
		frame.add(servicesWindow, BorderLayout.CENTER);

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
				new GetServices().createAndShowGUI();
			}
		});

	}

	private String[][] EnumerateServices() {

		int hSCM;
		int numberOfServices = 0;

		hSCM = Advapi32.OpenSCManager(null, null, SC_MANAGER_ENUMERATE_SERVICE);
		System.out.println(hSCM);
		if (hSCM != 0) {
			int buff = 0;
			int[] bytesNeeded = { 0 };
			int[] servicesReturned = { 0 };
			int[] resumeHandle = { 0 };
			int size = Util.getStructSize(EnumServiceStatus.class);

			System.out.println("ENUM_SERVICE_STATUS size=" + size);
			boolean result = Advapi32.EnumServicesStatus(hSCM, SERVICE_WIN32,
					SERVICE_STATE_ALL, buff, size, bytesNeeded,
					servicesReturned, resumeHandle);
			System.out.println("result 1= " + result);
			//System.out.println("GetLastError = "+Kernel32.GetLastError());
			System.out.println("bytesNeeded[0]=" + bytesNeeded[0]);
			numberOfServices = (bytesNeeded[0] / Util
					.getStructSize(EnumServiceStatus.class)) + 1;
			System.out.println("numberofservices=" + numberOfServices);

			EnumServiceStatus[] servicesInfo = new EnumServiceStatus[numberOfServices];
			for (int i = 0; i < numberOfServices; i++) {
				servicesInfo[i] = new EnumServiceStatus();
			}

			int[] resHandle = { 0 };
			byte[] ba = new byte[bytesNeeded[0]];
			for (int i = 0; i < ba.length; i++)
				ba[i] = 0;
			int buffer = Util.byteArrayToPtr(ba);
			result = Advapi32.EnumServicesStatus(hSCM, SERVICE_WIN32,
					SERVICE_STATE_ALL, buffer, bytesNeeded[0], bytesNeeded,
					servicesReturned, resHandle);

			//System.out.println("result 2 = "+result);

			servicesArray = new String[servicesReturned[0]][6];

			for (int i = 0; i < servicesReturned[0]; i++) {
				EnumServiceStatus ess = Util.ptrToStruct(buffer,
						EnumServiceStatus.class);
				System.out.println("ess[" + i + "].lpDisplayName = "
						+ ess.lpServiceName + " (" + ess.lpDisplayName + ")");
				buffer += Util.getStructSize(ess);
				hSCM = Advapi32.OpenSCManager(null, null, SC_MANAGER_CONNECT);

				int hService = Advapi32.OpenService(hSCM, ess.lpServiceName
						.toString(), GENERIC_READ);

				ServiceStatus serviceStatus = new ServiceStatus();
				Advapi32.QueryServiceStatus(hService, serviceStatus);
				String serviceState = "";
				switch (ess.dwCurrentState) {
				case SERVICE_STOPPED:
					serviceState = "Stopped";
					break;
				case SERVICE_START_PENDING:
					serviceState = "Starting";
					break;
				case SERVICE_STOP_PENDING:
					serviceState = "Shutting down";
					break;
				case SERVICE_RUNNING:
					serviceState = "Running";
					break;
				case SERVICE_CONTINUE_PENDING:
					serviceState = "Continuing";
					break;
				case SERVICE_PAUSE_PENDING:
					serviceState = "Pausing";
					break;
				case SERVICE_PAUSED:
					serviceState = "Paused";
					break;
				}

				int[] bytesReq = { 0 };
				Advapi32.QueryServiceConfig(hService, 0, 0, bytesReq);
				byte[] byteBuffer = new byte[bytesReq[0]];
				int serviceConfigPtr = Util.byteArrayToPtr(byteBuffer);

				Advapi32.QueryServiceConfig(hService, serviceConfigPtr,
						bytesReq[0], bytesReq);
				QueryServiceConfig queryServiceConfig = Util.ptrToStruct(
						serviceConfigPtr, QueryServiceConfig.class);
				//System.out.println(queryServiceConfig.dwServiceType);
				String startupType = "";

				switch (queryServiceConfig.dwStartType) {
				case SERVICE_AUTO_START:
					startupType = "Automatic";
					break;
				case SERVICE_BOOT_START:
				case SERVICE_DEMAND_START:
				case SERVICE_SYSTEM_START:
					startupType = "Manual";
					break;
				case SERVICE_DISABLED:
					startupType = "Disabled";
					break;
				}

				System.out.println("Start Type: "
						+ queryServiceConfig.dwStartType);
				System.out.println(queryServiceConfig.lpBinaryPathName);
				System.out.println(queryServiceConfig.lpDependenciese);
				String loadOrderGroup = queryServiceConfig.lpLoadOrderGroup
						.toString();
				int kIndex = 0;
				kIndex = (queryServiceConfig.lpBinaryPathName.toString())
						.indexOf("-k"); // (queryServiceConfig.lpBinaryPathName.toString()).contains("-k")
				System.out.println("kIndex: " + kIndex);
				if (kIndex > 0) {
					loadOrderGroup = ((queryServiceConfig.lpBinaryPathName
							.toString()).substring(kIndex));
					loadOrderGroup = loadOrderGroup.replace("-k", "").trim();
				}
				System.out.println("loadOrderGroup: " + loadOrderGroup);

				String serviceColumnArray[] = { ess.lpServiceName.toString(),
						ess.lpDisplayName.toString(), serviceState,
						startupType, loadOrderGroup };

				servicesArray[i] = serviceColumnArray;
			}
		}
		return servicesArray;
	}

}

class ServicesTableModel extends AbstractTableModel {
	private String[] columnNames = { "Name", "Description", "Status",
			"Startup Type", "Group" };

	private Object[][] data = { { "", "", "", "", "" } };

	public String getColumnName(int i) {
		return columnNames[i];
	}

	//	to change table data
	public void setData(Object[][] data) {
		this.data = data;
		fireTableDataChanged();
	}

	//	methods that you must implement
	public int getColumnCount() {//these methods are called by JTable
		return columnNames.length;
	}

	//	methods that you must implement
	public int getRowCount() {//these methods are called by JTable

		return data.length;
	}

	//	methods that you must implement table call this method to display data
	public Object getValueAt(int row, int col) {//these methods are called by JTable
		return data[row][col];
	}

}