/**
 * 
 */
package com.jzb.swt.j2s;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import swing2swt.layout.BorderLayout;

import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
public class TestWnd {

    private Label m_lblErrorMobile;
    private Text  m_amount;
    private Text  m_mobile;
    private Shell m_appShell;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            TestWnd window = new TestWnd();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open() throws Exception {

        final Display display = Display.getDefault();
        createContents();
        m_appShell.open();
        m_appShell.layout();

        _setWndPosition();

        m_appShell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                System.exit(0);
            }
        });

        while (!m_appShell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_appShell = new Shell();

        m_appShell.setImage(SWTResourceManager.getImage(TestWnd.class, "/Properties.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(5);
        m_appShell.setLayout(borderLayout);
        m_appShell.setSize(800, 255);
        m_appShell.setMinimumSize(new Point(800, 400));
        m_appShell.setText("AppTrkr");

        final Group mobileRechargeGroup = new Group(m_appShell, SWT.NONE);
        mobileRechargeGroup.setText("Mobile recharge");
        mobileRechargeGroup.setLayoutData(BorderLayout.CENTER);

        m_mobile = new Text(mobileRechargeGroup, SWT.BORDER);
        m_mobile.setBounds(80, 50, 80, 25);

        m_amount = new Text(mobileRechargeGroup, SWT.BORDER);
        m_amount.setBounds(80, 96, 80, 25);

        final Label mobileLabel = new Label(mobileRechargeGroup, SWT.NONE);
        mobileLabel.setBounds(27, 55, 55, 15);
        mobileLabel.setText("Mobile:");

        final Label importLabel = new Label(mobileRechargeGroup, SWT.NONE);
        importLabel.setText("Amount:");
        importLabel.setBounds(27, 99, 55, 15);

        final Button okButton = new Button(mobileRechargeGroup, SWT.NONE);
        okButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(final SelectionEvent e) {
                String mobile = m_mobile.getText();
                if(!_checkMobile(mobile)) {
                    m_lblErrorMobile.setBackground(SWTResourceManager.getColor(255, 128, 128));
                    m_lblErrorMobile.setText("Error: It must be numeric and be 9 digits long");
                }
                else {
                    m_lblErrorMobile.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                    m_lblErrorMobile.setText("");
                }
            }
        });
        okButton.setText("OK");
        okButton.setBounds(58, 140, 48, 25);

        final Button cancelButton = new Button(mobileRechargeGroup, SWT.NONE);
        cancelButton.setText("Cancel");
        cancelButton.setBounds(112, 140, 48, 25);

        m_lblErrorMobile = new Label(mobileRechargeGroup, SWT.NONE);
        m_lblErrorMobile.setBounds(166, 55, 310, 15);

    }

    private boolean _checkMobile(String mobile) {
        for (char c : mobile.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return mobile.length() == 9;
    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        m_appShell.setBounds(x, y, w, h);
    }

}