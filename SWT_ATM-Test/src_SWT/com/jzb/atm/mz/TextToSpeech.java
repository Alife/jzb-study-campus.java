/**
 * 
 */
package com.jzb.atm.mz;

import org.mozilla.interfaces.ITextToSpeech;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.IXPCOMError;
import org.mozilla.xpcom.XPCOMException;


/**
 * @author n63636
 * 
 */
public class TextToSpeech implements ITextToSpeech {

    public static TTSFree ttsFree = new TTSFree();
    
    public nsISupports queryInterface(String uuid) {
        if (!uuid.equals(NS_ISUPPORTS_IID) && !uuid.equals(ITextToSpeech.ITEXTTOSPEECH_IID)) {
            throw new XPCOMException(IXPCOMError.NS_ERROR_NOT_IMPLEMENTED);
        }
        return this;
    }

    /**
     * @see org.mozilla.interfaces.ITextToSpeech#say(java.lang.String, boolean)
     */
    public void say(String text, boolean queue) {
        ttsFree.say(text, queue);
    }
}
