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
public class CardReader_Sync implements ICardReader {

    public nsISupports queryInterface(String uuid) {
        if (!uuid.equals(NS_ISUPPORTS_IID) && !uuid.equals(ICardReader.ICARDREADER_IID)) {
            throw new XPCOMException(IXPCOMError.NS_ERROR_NOT_IMPLEMENTED);
        }
        return this;
    }

    /**
     * @see org.mozilla.interfaces.ICardReader#isReady()
     */
    public boolean isReady() {

        return true;
    }

    /**
     * @see org.mozilla.interfaces.ICardReader#readCard(org.mozilla.interfaces.ICardReaderCallBack, java.lang.String)
     */
    public void readCard(ICardReaderCallBack callback, String cardCode) {
        callback.call(null, "datos_leidos", new CardReaderError("HardwareError", "Hardware is not working properly"));
    }

}
