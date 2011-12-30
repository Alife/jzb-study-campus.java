/**
 * 
 */
package com.jzb.nsf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author n000013
 * 
 */
public class DGetUsrPwd extends Dialog {

    public class T_UsrPwd {

        public T_UsrPwd(String u, String p) {
            user = u;
            pwd = p;
        }

        String user;
        String pwd;
    }

    private T_UsrPwd m_result;
    private Shell    m_shell;
    private Text     m_txtUser;
    private Text     m_txtPwd;

    /**
     * Create the dialog.
     * 
     * @param parent
     * @param style
     */
    public DGetUsrPwd(Shell parent) {
        super(parent, SWT.APPLICATION_MODAL);
        setText("Set GCal User and Password");
    }

    /**
     * Open the dialog.
     * 
     * @return the result
     */
    public T_UsrPwd open() {
        createContents();
        m_shell.open();
        m_shell.layout();
        _setWndPosition();
        Display display = getParent().getDisplay();
        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return m_result;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        m_shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        m_shell.setSize(341, 165);
        m_shell.setText("Set Google Calendar User and Password");
        {
            Label lblUser = new Label(m_shell, SWT.NONE);
            lblUser.setBounds(22, 13, 59, 16);
            lblUser.setText("User:");
        }
        {
            Label lblPassword = new Label(m_shell, SWT.NONE);
            lblPassword.setBounds(22, 51, 59, 16);
            lblPassword.setText("Password: ");
        }
        {
            m_txtUser = new Text(m_shell, SWT.BORDER);
            m_txtUser.setText("myuser@gmail.com");
            m_txtUser.setBounds(87, 10, 219, 22);
        }
        {
            m_txtPwd = new Text(m_shell, SWT.BORDER | SWT.PASSWORD);
            m_txtPwd.setBounds(87, 48, 219, 22);
        }
        {
            Button btnChange = new Button(m_shell, SWT.NONE);
            btnChange.addSelectionListener(new SelectionAdapter() {

                @SuppressWarnings("synthetic-access")
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (m_txtUser.getText() == null || m_txtUser.getText().length() == 0 || m_txtPwd.getText() == null || m_txtPwd.getText().length() == 0) {
                        MessageBox mb = new MessageBox(m_shell, SWT.ICON_ERROR | SWT.OK);
                        mb.setText("Error in data");
                        mb.setMessage("User and Password fields cannot be empty");
                        mb.open();
                    } else {
                        m_result = new T_UsrPwd(m_txtUser.getText(), m_txtPwd.getText());
                        m_shell.close();
                    }
                }
            });
            btnChange.setBounds(142, 89, 77, 26);
            btnChange.setText("change");
        }
        {
            Button btnCancel = new Button(m_shell, SWT.NONE);
            btnCancel.addSelectionListener(new SelectionAdapter() {

                @SuppressWarnings("synthetic-access")
                @Override
                public void widgetSelected(SelectionEvent e) {
                    m_shell.close();
                }
            });
            btnCancel.setBounds(229, 89, 77, 26);
            btnCancel.setText("cancel");
        }

    }
    private void _setWndPosition() {
        Rectangle r1 = Display.getDefault().getBounds();
        Rectangle r2= m_shell.getBounds();
        int x = (r1.width - r2.width) / 2;
        int y = (r1.height - r2.height) / 2;
        m_shell.setLocation(x,y);
    }
}
