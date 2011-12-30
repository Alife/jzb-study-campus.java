package com.JWinAPI;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class WinManagerUI  implements ActionListener, ListSelectionListener {
	
	private AppWindow winMain;
	private DefaultTableModel model;
	private JTable procTable;
    private JPanel procPane;
    private JPanel toolPane;
    private JPanel statPane;    
    private static JWinAPI wapi;
    
    private String strTitleSelected;
    private int iPIDSelected;
    private int iWHNDSelected;
    private String strExecSelected;   
    private int iAppsFound = 0;    
    
	public void doUI( JWinAPI jwapi )  {
		wapi = jwapi;
		Vector vector = new Vector();

		winMain = new AppWindow( "JWINApi Window Manager V1.0", new Dimension( 600, 400));

		doBuildTable();		
        
        toolPane = new JPanel();
        toolPane.setLayout(new FlowLayout( FlowLayout.LEFT ) );
        toolPane.setBackground( new Color(240,240,240) );
        
        statPane = new JPanel();
        statPane.setBackground( new Color(240,240,240) );
        JLabel sLabel = new JLabel( "JWINAPI Demo program -- Sample Window Manager");
        sLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statPane.add( sLabel );
        
        JButton jb1 = new JButton("Minimize");
        jb1.setToolTipText("Minimize Window");
        jb1.setActionCommand("minimize");
        jb1.addActionListener(this);

        JButton jb2 = new JButton("Maximize");
        jb2.setToolTipText("Maximize Window");
        jb2.setActionCommand("maximize");
        jb2.addActionListener(this);

       
        JButton jb3 = new JButton("Kill");
        jb3.setToolTipText("Kill process");
        jb3.setActionCommand("kill");
        jb3.addActionListener(this);

        JButton jb4 = new JButton("On top");
        jb4.setToolTipText("Set always on top");
        jb4.setActionCommand("aot");
        jb4.addActionListener(this);

        JButton jb5 = new JButton("Activate");
        jb5.setToolTipText("Activate the window (Bring to front)");
        jb5.setActionCommand("activate");
        jb5.addActionListener(this);
        
        JButton jb6 = new JButton("Refresh");
        jb6.setToolTipText("Refresh");
        jb6.setActionCommand("refresh");
        jb6.addActionListener(this);
        
        
        JLabel lAppsFound = new JLabel("Apps found: " + iAppsFound );
        toolPane.add( jb1 );
        toolPane.add( jb2 );        
        toolPane.add( jb3 );        
        toolPane.add( jb4 );
        toolPane.add( jb5 );        
    //    toolPane.add( jb6 );        
        toolPane.add( lAppsFound );        

        toolPane.setBorder(BorderFactory.createEtchedBorder());
        statPane.setBorder(BorderFactory.createEtchedBorder());        
        
        JScrollPane scrollPane = new JScrollPane(procTable);
        procPane = new JPanel();
        procPane.setLayout(new BorderLayout());
        procPane.add(toolPane, BorderLayout.NORTH );        
        procPane.add(scrollPane, BorderLayout.CENTER );
        procPane.add( statPane, BorderLayout.SOUTH );
		        
        winMain.getContentPane().setLayout(new BorderLayout());        
		winMain.getContentPane().add( procPane );
		winMain.setVisible( true );

		
	}
	
    //This method is required by ListSelectionListener.
    public void valueChanged(ListSelectionEvent e) {
    	
        if (e.getValueIsAdjusting() == false) {
        	strTitleSelected = (String) procTable.getValueAt(procTable.getSelectedRow() , 0 );
        	iPIDSelected = Integer.parseInt((String) procTable.getValueAt(procTable.getSelectedRow() , 1 ));  
        	iWHNDSelected = Integer.parseInt((String) procTable.getValueAt(procTable.getSelectedRow() , 2 )); 
        	strExecSelected = (String) procTable.getValueAt(procTable.getSelectedRow() , 3 );
        }
    }
    
    /**
     *  
     */
    public void actionPerformed(ActionEvent e) {
    	try {
            String command = e.getActionCommand();
            if (command.equalsIgnoreCase("minimize")) {
            	wapi.doShowWindow(iWHNDSelected, SWConstants.SW_SHOWMINIMIZED );
            } else if (command.equalsIgnoreCase("maximize")) {
            	wapi.doShowWindow(iWHNDSelected, SWConstants.SW_SHOWMAXIMIZED );            	
            } else if (command.equalsIgnoreCase("aot")) {
            	wapi.doAlwaysOnTop(iWHNDSelected );            	
            } else if (command.equalsIgnoreCase("kill")) {
            	wapi.doKillProcess( iWHNDSelected );            	
            } else if (command.equalsIgnoreCase("activate")) {
            	wapi.doAppActivate( strTitleSelected );            	
            } 
        } catch (Exception ex ) {
        	   wapi.doMessageBox("Error in actionListener: " 
        			   + ex.toString(), "WinManager", MBConstants.MB_OK 
        			   + MBConstants.MB_ICONERROR );
           }

    }
	
    /**
     * Refresh table
     *
     */
    private void doRefresh() {
    	for ( int i = 0; i < model.getRowCount(); i++  ) {
    		model.removeRow( i );
    	}
    	doBuildTable();
    }
    
    /**
     * Build process table
     *
     */
    private void doBuildTable() {
		model = new DefaultTableModel();		

        model.addColumn("Title");		
        model.addColumn("PID");
        model.addColumn("WHND");        

        model.addColumn("Exec name");        
        iAppsFound = 0;
        
        JWinProcess[] jwp = wapi.doGetWindowProcesses();
        
        for ( int i = 0; i < jwp.length; i++ ) {
        	//if ( jwp[ i ].isVisible() && jwp[ i ].hasTitle() ) {
        	if ( jwp[ i ].hasTitle()  ) {

        		model.addRow(  new Object[] {
        				jwp[ i ].getWTitle(),
        				String.valueOf( jwp[ i ].getPID() ), 
        				String.valueOf( jwp[ i ].getWHND() ),        				
        				jwp[ i ].getExecName()} );
        		iAppsFound++;        		
        	}
        }
        procTable = new JTable( model );
        procTable.getSelectionModel().addListSelectionListener( this );
        procTable.setBackground( new Color(255,255,255) );
        
    }
}
