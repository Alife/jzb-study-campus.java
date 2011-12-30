package com.jzb.atm.mz;

import org.mozilla.interfaces.nsIComponentRegistrar;
import org.mozilla.interfaces.nsIFactory;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.IXPCOMError;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.xpcom.XPCOMException;

public abstract class AbstractXPCOM implements nsIFactory {

    private final String _iid;
    private final String _cid;
    private final String _Class_Name;
    private final String _Contract_ID;

    public AbstractXPCOM(String iid, String cid, String Class_Name, String Contract_ID) {
        _iid = iid;
        _cid = cid;
        _Class_Name = Class_Name;
        _Contract_ID = Contract_ID;
    }

    public String getCID() {
        return _cid;
    }

    public String getIID() {
        return _iid;
    }

    public abstract AbstractXPCOM getInstance();

    public nsISupports queryInterface(String uuid) {
        if (!uuid.equals(NS_ISUPPORTS_IID) && (!uuid.equals(NS_IFACTORY_IID)) && (!uuid.equals(getIID()))) {
            throw new XPCOMException(IXPCOMError.NS_ERROR_NOT_IMPLEMENTED);
        }
        return this;
    }

    /* nsIFactory Implementation */
    public nsISupports createInstance(nsISupports aOuter, String iid) {
        if (aOuter != null) {
            throw new XPCOMException(IXPCOMError.NS_ERROR_NO_AGGREGATION);
        }
        if (!iid.equals(getIID()) && !iid.equals(nsISupports.NS_ISUPPORTS_IID)) {
            throw new XPCOMException(IXPCOMError.NS_ERROR_INVALID_ARG);
        }
        return getInstance();
    }

    public void lockFactory(boolean lock) {
        /* Don't know what to do here... Doesn't seem to matter though */
    }

    protected void _register() {
        Mozilla mozilla = Mozilla.getInstance();
        try {
            // TODO La primera vez me ha solicitado iniciarlo, ver si esto es necesario o no..
            // MozillaInitializer.initMozilla();
        } catch (Exception e) {
        }

        nsIComponentRegistrar registrar = mozilla.getComponentRegistrar();
        registrar.registerFactory(_cid, _Class_Name, _Contract_ID, getInstance());

    }
}
