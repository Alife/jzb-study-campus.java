/**
 * 
 */
package com.jzb.atm.mz;

import java.util.HashMap;
import org.mozilla.interfaces.IXPCOM_ATMBridge;
import org.mozilla.interfaces.nsISupports;

/**
 * @author n63636
 * 
 */
public class XPCOM_ATMBridge extends AbstractXPCOM implements IXPCOM_ATMBridge {

    public static final String                  CID         = "{6fde3824-7665-11dc-8314-0800200c9a6A}";
    public static final String                  CONTRACT_ID = "@com.jzb.atm/XPCOMBridge;1";
    public static final String                  CLASS_NAME  = "XPCOMBridge";

    private static final String                 VERSION     = "1.23.45";

    private static XPCOM_ATMBridge              INSTANCE    = null;

    private static HashMap<String, nsISupports> m_bundles   = new HashMap<String, nsISupports>();

    public XPCOM_ATMBridge() {
        super(IXPCOM_ATMBridge.IXPCOM_ATMBRIDGE_IID, CID, CLASS_NAME, CONTRACT_ID);
    }

    public static void register() {
        INSTANCE = new XPCOM_ATMBridge();
        INSTANCE._register();
    }

    public static void addBundle(String API_Name, nsISupports bundle) {
        m_bundles.put(API_Name, bundle);
    }

    /**
     * @see org.mozilla.interfaces.AbstractXPCOM#getInstance()
     */
    @Override
    public AbstractXPCOM getInstance() {
        return INSTANCE;
    }

    /**
     * @see org.mozilla.interfaces.IXPCOM_ATMBridge#getAPI(java.lang.String)
     */
    public nsISupports getAPI(String API_Name) {
        return m_bundles.get(API_Name);
    }

    /**
     * @see org.mozilla.interfaces.IXPCOM_ATMBridge#getVersion()
     */
    public String getVersion() {
        return VERSION;
    }

}
