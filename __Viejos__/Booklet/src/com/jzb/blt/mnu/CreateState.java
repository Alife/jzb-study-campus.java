/**
 * 
 */
package com.jzb.blt.mnu;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

import com.jzb.blt.StoreManager;
import com.jzb.flow.EnumCommand;
import com.jzb.flow.Event;
import com.jzb.flow.Flow;
import com.jzb.flow.FormState;
import com.jzb.j2me.util.Utils;

/**
 * @author PS00A501
 * 
 */
public class CreateState extends FormState {

    private TextField m_tfName;
    private TextField m_tfText;

    private MenuItem  m_menuItem;

    /**
     * 
     */
    public CreateState() {
        this(null, null, null);
    }

    /**
     * @param owner
     * @param name
     * @param title
     */
    public CreateState(Flow owner, String name, String title) {
        super(owner, name, title);
    }

    public void commandAction(Command cmd, Displayable disp) {

        if (cmd instanceof EnumCommand) {
            EnumCommand ec = (EnumCommand) cmd;
            if (ec.getCode() == Command.OK) {
                saveInfo();
            } else {
                super.commandAction(cmd, disp);
            }
        }
    }

    /**
     * @see com.jzb.flow.FormState#createItems()
     */
    protected void createItems() {

        m_tfName = new TextField("Name:", "", 80, TextField.ANY | TextField.SENSITIVE);
        append(m_tfName);

        m_tfText = new TextField("Text:", "", 65536, TextField.ANY | TextField.SENSITIVE);
        append(m_tfText);

        addCommand(EnumCommand.ECMD_OK);
        addCommand(EnumCommand.ECMD_CANCEL);
    }

    /**
     * @see com.jzb.flow.FormState#innerActivate(com.jzb.flow.Event)
     */
    protected void innerActivate(Event ev) {

        m_tfName.setString("");
        m_tfText.setString("");
        m_menuItem = (MenuItem) ev.getData();
        if (m_menuItem == null) {
            _fireEvent(Event.EV_CANCEL);
        }
    }

    private int saveFileInfo(String txt) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.write(txt.getBytes());
            dos.close();
            byte buffer[] = baos.toByteArray();

            return StoreManager.saveFile(buffer);
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error saving file info: " + Utils.getExceptionMsg(ex));
            return -1;
        }
    }

    private void saveInfo() {

        String name = m_tfName.getString();
        String txt = m_tfText.getString();
        boolean isFile = (txt != null && txt.length() > 0);
        MenuItem mi = new MenuItem(name, isFile);

        int index = saveFileInfo(txt);
        mi.setFileRecordIndex(index);
        m_menuItem.addChild(mi);

        if (index != -1 && saveMenuInfo())
            _fireEvent(Event.EV_OK);
    }

    private boolean saveMenuInfo() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            m_menuItem.getRootParent().writeExternal(dos);
            dos.close();
            byte buffer[] = baos.toByteArray();

            StoreManager.saveMenu(buffer);

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            _fireEvent(Event.EV_EXCEPTION, "Error saving menu info: " + Utils.getExceptionMsg(ex));
            return false;
        }
    }

}
