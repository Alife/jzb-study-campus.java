// VersionInfoUI.java
// Gayathri Singh, March 2008, gayathri@byteblend.com

/* The VersionInfoUI class provides a GUI frontend for the VersionInfo class.
 
   Usage: 
     java -cp c:\jinvoke\jinvoke.jar;. jinvoke.win32demos.versioninfo.VersionInfoUI  [ file-path ]
*/
package jinvoke.win32demos.versioninfo;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.filechooser.FileFilter;

public class VersionInfoUI extends javax.swing.JFrame {
    JFileChooser fc;
    /** Creates new form GetFileVersionUI */
    public VersionInfoUI(String filepath) {
        initComponents();
        txtFieldSourceFile.setText(filepath);
        jScrollPane1.getViewport().setBackground(Color.WHITE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayFileInfo();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtFieldSourceFile = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        btnGetVersionInfo = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        keyValueTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("File Version Information");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "File Version Information"));

        jLabel1.setText("Select a File:");
        
        btnBrowse.setText("...");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        btnGetVersionInfo.setText("Get Version Information");
        btnGetVersionInfo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetVersionInfoActionPerformed(evt);
            }
        });

        keyValueTable.setModel(new KeyValueTableModel());
        keyValueTable.setShowHorizontalLines(false);
        keyValueTable.setShowVerticalLines(false);
        jScrollPane1.setViewportView(keyValueTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                    .addComponent(btnGetVersionInfo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFieldSourceFile, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFieldSourceFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGetVersionInfo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setText("Close");
        jButton1.addActionListener(new ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		System.exit(0);
        	}
        });
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(25, 25, 25))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        //Set up the file chooser.
        if (fc == null) 
            fc = new JFileChooser();

            //Add a custom file filter and disable the default
            //(Accept All) file filter.
        fc.addChoosableFileFilter(new AppFilter());
        fc.setAcceptAllFileFilterUsed(false);

        //Show it.
        int returnVal = fc.showDialog(this, "Select File");

        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            txtFieldSourceFile.setText(file.getPath());
        }
        //Reset the file chooser for the next time it's shown.
        fc.setSelectedFile(null);
    }//GEN-LAST:event_btnBrowseActionPerformed
    
        private void btnGetVersionInfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetVersionInfoActionPerformed
            displayFileInfo();
        }//GEN-LAST:event_btnGetVersionInfoActionPerformed

		private void displayFileInfo() {
			VersionInfo fileVersionInfo = new VersionInfo(txtFieldSourceFile.getText());
            //System.out.println(fileVersionInfo);
            String [][] keyValue = new String[][] { {"Version        ", fileVersionInfo.fileVersionString }, 
                                                  { "Attributes      ", fileVersionInfo.fileAttributes },
                                                  { "Operating System", fileVersionInfo.fileOS },
                                                  
                                                  { "Type            ", fileVersionInfo.fileType },
                                                  { "SubType         ", fileVersionInfo.fileSubType },
                                                  { "Language        ", fileVersionInfo.language },

                                                  { "Comments        ", fileVersionInfo.comments },
                                                  { "Internal Name   ", fileVersionInfo.internalName },
                                                  { "Product Name    ", fileVersionInfo.productName },

                                                  { "Company Name    ", fileVersionInfo.companyName },
                                                  { "Legal Copyright ", fileVersionInfo.legalCopyright },
                                                  { "Product Version ", fileVersionInfo.productVersion },
      	
                                                  { "File Description", fileVersionInfo.fileDescription },
                                                  { "Legal Trademarks", fileVersionInfo.legalTrademarks },
                                                  { "Private Build   ", fileVersionInfo.privateBuild },
  	
                                                  { "File Version    ", fileVersionInfo.fileVersion },
                                                  { "Original Filename", fileVersionInfo.originalFilename },
                                                  { "Special Build   ", fileVersionInfo.specialBuild } };
                                                  
            ((KeyValueTableModel) keyValueTable.getModel()).setData(keyValue);
		}
   
    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                	String path = "C:\\Windows\\Notepad.exe";
                	if (args.length == 1)
                		path = args[0];
                	
                	VersionInfoUI fileVersionUI = new VersionInfoUI(path);
                    fileVersionUI.setVisible(true);
                }
            });
         } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnGetVersionInfo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable keyValueTable;
    private javax.swing.JTextField txtFieldSourceFile;
    // End of variables declaration//GEN-END:variables
    
}

class KeyValueTableModel extends AbstractTableModel {
	
	private String[] columnNames = { "Property", "Value"};
	private Object[][] data = { { "", ""} };

    @Override
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

class AppFilter extends FileFilter {

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals("exe") ||
                extension.equals("dll") ||
                extension.equals("drv") ||
                extension.equals("vxd") ||
                extension.equals("lib")) {
                    return true;
            } else {
                return false;
            }
        }

        return false;
    }

    //The description of this filter
    public String getDescription() {
        return "Applications";
    }
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
}