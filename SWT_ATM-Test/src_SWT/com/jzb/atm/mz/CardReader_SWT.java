/**
 * 
 */
package com.jzb.atm.mz;

import org.mozilla.interfaces.ICardReader;
import org.mozilla.interfaces.ICardReaderCallBack;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.IXPCOMError;
import org.mozilla.xpcom.XPCOMException;

/**
 * @author n63636
 * 
 */
public class CardReader_SWT implements ICardReader {

    // ------------------------------------
    public interface SWT_Callback {

        public void onComplete(String data, boolean inError);
    }

    // ------------------------------------
    public interface SWT_Support {

        public boolean isReady();

        public void userInteraction(SWT_Callback ownerCB, String cardCode);
    }

    private SWT_Support m_swt_support;

    public CardReader_SWT(SWT_Support swt_support) {
        m_swt_support = swt_support;
    }

    public nsISupports queryInterface(String uuid) {
        if (!uuid.equals(NS_ISUPPORTS_IID) && !uuid.equals(ICardReader.ICARDREADER_IID)) {
            throw new XPCOMException(IXPCOMError.NS_ERROR_NOT_IMPLEMENTED);
        }
        return this;
    }

    public boolean isReady() {
        return m_swt_support.isReady();
    }

    public void readCard(final ICardReaderCallBack callBack, final String cardCode) {

        SWT_Callback ownerCB = new SWT_Callback() {

            public void onComplete(String data, boolean inError) {

                // OJO que suelta una XPCOMException si el JS se la pega
                // Y es una RuntimeException (que no se declara como throws)
                if (inError)
                    callBack.call(null, null, new CardReaderError("HardwareError", "Hardware is not working properly"));
                else
                    callBack.call(null, data, null);
            }
        };

        m_swt_support.userInteraction(ownerCB, cardCode);
    }

}
