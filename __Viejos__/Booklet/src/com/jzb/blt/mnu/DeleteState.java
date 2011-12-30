/**
 * 
 */
package com.jzb.blt.mnu;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.jzb.blt.StoreManager;
import com.jzb.flow.BaseState;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class DeleteState extends BaseState {

    /**
     * 
     */
    public DeleteState() {
        this(null, null);
    }

    /**
     * @param owner
     * @param name
     */
    public DeleteState(Flow owner, String name) {
        super(owner, name);
    }

    /**
     * @see com.jzb.flow.BaseState#activate(com.jzb.flow.Event)
     */
    public void activate(Event ev) {

        MenuItem mi = (MenuItem) ev.getData();
        try {
            if (mi != null) {
                MenuItem root = mi.getRootParent();
                deleteAllFiles(mi);
                mi.getParent().removeChild(mi);
                saveMenuInfo(root);
            }
            _fireEvent(Event.EV_OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error deleting item: " + Utils.getExceptionMsg(ex));
        }

    }

    private void deleteAllFiles(MenuItem mi) throws Exception {
        if (mi.isFile()) {
            StoreManager.deleteFile(mi.getFileRecordIndex());
        } else {
            for (int n = 1; n < mi.getNumItems(); n++) {
                deleteAllFiles(mi.getChild(n));
            }
        }
    }

    private void saveMenuInfo(MenuItem root) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        root.writeExternal(dos);
        dos.close();
        byte buffer[] = baos.toByteArray();

        StoreManager.saveMenu(buffer);
    }

}
