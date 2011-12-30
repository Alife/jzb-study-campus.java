/**
 * 
 */
package com.jzb.atm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mozilla.interfaces.ITextToSpeech;
import org.mozilla.interfaces.nsIConsoleListener;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIConsoleService;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.IXPCOMError;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

import com.jzb.atm.mz.CardReader_SWT;
import com.jzb.atm.mz.CardReader_Sync;
import com.jzb.atm.mz.TextToSpeech;
import com.jzb.atm.mz.XPCOM_ATMBridge;
import com.jzb.atm.mz.XULRunnerInitializer;
import com.jzb.atm.mz.CardReader_SWT.SWT_Callback;
import com.swtdesigner.SWTResourceManager;
import swing2swt.layout.BorderLayout;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class CopyOfATMTestAppWnd {

    // -------------------------------------------------------------------------------
    private class SWT_CardReaderHW implements CardReader_SWT.SWT_Support {

        public boolean isReady() {
            return m_btnIsReady.getSelection();
        }

        public void userInteraction(final SWT_Callback ownerCB, final String cardCode) {

            Display.getDefault().asyncExec(new Runnable() {

                public void run() {
                    _userInteraction(ownerCB, cardCode);
                }
            });
        }

        private void _userInteraction(final SWT_Callback ownerCB, final String cardCode) {
            m_btnCardReader.setEnabled(true);
            m_btnCardReader.setData(ownerCB);
            m_btnError.setEnabled(true);
            m_txtCardCode.setText(cardCode);
            m_txtCardCode.setEnabled(true);
            m_txtCardInfo.setEnabled(true);
        }

        public void onReadCardComplete() {

            SWT_Callback callBack = (SWT_Callback) m_btnCardReader.getData();
            String data = m_txtCardInfo.getText();
            boolean inError = m_btnError.getSelection();

            m_btnCardReader.setData(null);
            m_btnCardReader.setEnabled(false);
            m_txtCardCode.setText("");
            m_txtCardCode.setEnabled(false);
            m_txtCardInfo.setText("");
            m_txtCardInfo.setEnabled(false);
            m_btnError.setEnabled(false);

            callBack.onComplete(data, inError);
        }

    }

    // -------------------------------------------------------------------------------
    private Shell            m_shell;
    private Text             m_txtCardCode;
    private Text             m_txtCardInfo;
    private Button           m_btnError;
    private Button           m_btnCardReader;
    private SWT_CardReaderHW m_cardReaderHW = new SWT_CardReaderHW();
    private Browser          m_browser;
    private Button           m_btnIsReady;

    /**
     * Launch the application
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            XULRunnerInitializer.initialize();

            CopyOfATMTestAppWnd window = new CopyOfATMTestAppWnd();
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

        _setWndPosition();
        _initXPCOM();
        _initFields();
        _initJSErrorHook();

        m_shell.open();
        m_shell.layout();

        m_shell.addShellListener(new ShellAdapter() {

            @Override
            public void shellClosed(ShellEvent e) {
                System.exit(0);
            }
        });

        while (!m_shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

    }

    /**
     * Create contents of the window
     */
    protected void createContents() {

        m_shell = new Shell();
        m_shell.setLayout(new BorderLayout(0, 0));
        m_shell.setText("ATM Window Controller Test");
        m_shell.setSize(650, 483);

        m_shell.setImage(SWTResourceManager.getImage(CopyOfATMTestAppWnd.class, "/Properties.ico"));
        {
            m_browser = new Browser(m_shell, SWT.BORDER | SWT.MOZILLA);
            //m_browser = new Browser(m_shell, SWT.BORDER);
        }

        final Group cardReaderSimulatorGroup = new Group(m_shell, SWT.NONE);
        cardReaderSimulatorGroup.setLayoutData(BorderLayout.SOUTH);
        cardReaderSimulatorGroup.setText("Card reader simulator");

        m_btnIsReady = new Button(cardReaderSimulatorGroup, SWT.CHECK);
        m_btnIsReady.setSelection(true);
        m_btnIsReady.setText("Is CardReader Ready");
        m_btnIsReady.setBounds(10, 29, 151, 16);

        Group group = new Group(cardReaderSimulatorGroup, SWT.NONE);
        group.setBounds(10, 51, 307, 100);
        {
            Label lblCardCode = new Label(group, SWT.NONE);
            lblCardCode.setBounds(10, 29, 88, 16);
            lblCardCode.setText("CARD Code:");
        }

        Label lblCardInfo = new Label(group, SWT.NONE);
        lblCardInfo.setText("CARD Info:");
        lblCardInfo.setBounds(10, 57, 88, 16);
        {
            m_txtCardCode = new Text(group, SWT.BORDER);
            m_txtCardCode.setBounds(104, 26, 61, 22);
            m_txtCardCode.setEnabled(false);
        }

        m_txtCardInfo = new Text(group, SWT.BORDER);
        m_txtCardInfo.setEnabled(false);
        m_txtCardInfo.setBounds(104, 56, 124, 22);
        m_btnCardReader = new Button(group, SWT.NONE);
        m_btnCardReader.setBounds(242, 54, 52, 26);
        m_btnCardReader.setEnabled(false);
        m_btnCardReader.setText("Send");
        {
            m_btnError = new Button(group, SWT.CHECK);
            m_btnError.setEnabled(false);
            m_btnError.setBounds(242, 29, 96, 17);
            m_btnError.setText("Error");
        }
        m_btnCardReader.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                m_cardReaderHW.onReadCardComplete();
            }
        });

    }

    private void _setWndPosition() {
        Rectangle r = Display.getDefault().getBounds();
        int x = (r.width - m_shell.getSize().x) / 2;
        int y = (r.height - m_shell.getSize().y) / 2;
        m_shell.setLocation(x, y);
    }

    private void _initFields() throws Exception {
        //m_browser.setUrl("file:///C:/WKSPs/Consolidado/SWT_ATM-Test/resources/page.html");
        m_browser.setUrl("file:///C:/WKSPs/Consolidado/SWT_ATM-Test/resources/index.html");
        
        // m_browser.setUrl("http://localhost/jzbTest/page.html");
    }

    private void _initXPCOM() {

        XPCOM_ATMBridge.register();
        // XPCOM_ATMBridge.addBundle("ICardReader", new CardReader_Sync());
        XPCOM_ATMBridge.addBundle("ICardReader", new CardReader_SWT(m_cardReaderHW));

        XPCOM_ATMBridge.addBundle("ITextToSpeech", new TextToSpeech());

    }

    private void _initJSErrorHook() {

        // nsIWebBrowser wb = (nsIWebBrowser) m_browser.getWebBrowser();

        Mozilla mz = Mozilla.getInstance();
        nsIServiceManager sm = mz.getServiceManager();
        nsIConsoleService con = (nsIConsoleService) sm.getServiceByContractID("@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID);

        nsIConsoleListener lis = new nsIConsoleListener() {

            public nsISupports queryInterface(String uuid) {
                if (!uuid.equals(NS_ISUPPORTS_IID) && !uuid.equals(nsIConsoleListener.NS_ICONSOLELISTENER_IID)) {
                    throw new XPCOMException(IXPCOMError.NS_ERROR_NOT_IMPLEMENTED);
                }
                return this;
            }

            public void observe(nsIConsoleMessage aMsg) {
                // JavaScript Error
                System.out.println("console -> " + aMsg.getMessage());

            }
        };
        con.registerListener(lis);
    }
}
