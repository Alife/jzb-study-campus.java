/**
 * 
 */
package com.jzb.atm.mz;

import org.mozilla.interfaces.ICardReaderError;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.IXPCOMError;
import org.mozilla.xpcom.XPCOMException;

/**
 * @author n63636
 * 
 */
public class CardReaderError implements ICardReaderError {

    private String m_name;
    private String m_msg;

    public CardReaderError(String name, String msg) {
        m_name = name;
        m_msg = msg;
    }

    public nsISupports queryInterface(String uuid) {
        if (!uuid.equals(NS_ISUPPORTS_IID) && !uuid.equals(ICardReaderError.ICARDREADERERROR_IID)) {
            throw new XPCOMException(IXPCOMError.NS_ERROR_NOT_IMPLEMENTED);
        }
        return this;
    }

    public String getName() {
        return m_name;
    }

    public String getMsg() {
        return m_msg;
    }

}
