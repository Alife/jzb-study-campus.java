/**
 * 
 */
package com.jzb.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class AppWnd {



    private Text m_Text;
    protected Shell             shell;


    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            AppWnd window = new AppWnd();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open() {
        final Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();

        _setWndPosition(shell);
        _initFields();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        shell = new Shell();
        shell.setMinimumSize(new Point(100, 100));
        shell.setImage(SWTResourceManager.getImage(AppWnd.class, "/Publish.ico"));
        final BorderLayout borderLayout = new BorderLayout(0, 0);
        borderLayout.setVgap(10);
        shell.setLayout(borderLayout);
        shell.setSize(395, 201);
        shell.setText("ImgOrg");

        final Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayoutData(BorderLayout.CENTER);
        composite.setLayout(new BorderLayout(0, 0));

        m_Text = new Text(composite, SWT.BORDER);
        m_Text.setLayoutData(BorderLayout.SOUTH);

    }

    private void _setWndPosition(Shell shell) {
        Rectangle r = Display.getDefault().getBounds();
        int w = (80 * r.width) / 100;
        int h = (80 * r.height) / 100;
        int x = (r.width - w) / 2;
        int y = (r.height - h) / 2;
        shell.setBounds(x, y, w, h);
    }
    

    
    private void _initFields() {
    }
    

}
