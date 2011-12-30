package com.jzb.blt.serial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;

import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;

import com.jzb.blt.Constants;
import com.jzb.blt.StoreManager;
import com.jzb.blt.mnu.MenuItem;
import com.jzb.blt.txt.TextData;
import com.jzb.blt.txt.TextManager;
import com.jzb.j2me.util.GlobalContext;

public class SPortManager {

    public interface ISendListener {

        public void done(boolean wasCanceled);

        public void setFileName(String name);

        public void setTotalDataSize(int size);

        public void signalException(Throwable th);

        public void updateSentAmount(int size);
    }

    public interface IReceiveListener {

        public void done(boolean wasCanceled);

        public void endRSUpdate();

        public void setFileName(String name);

        public void setTotalDataSize(int size);

        public void signalException(Throwable th);

        public void startRSUpdate();

        public void updateReceivedAmount(int size);
    }

    public interface IWaitConnetion {

        public void done(boolean wasCanceled);

        public void signalException(Throwable th);
    }

    private static final String CONNECION_STRING = ";baudrate=115200;bitsperchar=8;stopbits=1;parity=even;blocking=on;autocts=on;autorts=on";

    private CommConnection      m_commCon;
    private Thread              m_thread;
    private boolean             m_wasCanceled;

    public SPortManager() {
        m_wasCanceled = false;
    }

    public void AsyncConnectSPort(final IWaitConnetion iwc) {
        m_thread = new Thread(new Runnable() {

            public void run() {
                try {
                    _connectSPort();
                    iwc.done(m_wasCanceled);
                } catch (InterruptedException iex) {
                    iwc.done(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    iwc.signalException(ex);
                }
            }
        });
        m_thread.start();
    }

    public void AsyncReceiveData(final IReceiveListener irl) {
        m_thread = new Thread(new Runnable() {

            public void run() {
                try {
                    _receiveData(irl);
                    irl.done(m_wasCanceled);
                } catch (InterruptedException iex) {
                    irl.done(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    irl.signalException(ex);
                }
            }
        });
        m_thread.start();
    }

    public void AsyncSendData(final ISendListener isl) {
        m_thread = new Thread(new Runnable() {

            public void run() {
                try {
                    _sendData(isl);
                    isl.done(m_wasCanceled);
                } catch (InterruptedException iex) {
                    isl.done(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    isl.signalException(ex);
                }
            }
        });
        m_thread.start();
    }

    public void cancel() {
        m_wasCanceled = true;
        if (m_thread != null) {
            m_thread.interrupt();
        }
    }

    public void disconnectSPort() throws Exception {
        if (m_commCon != null)
            m_commCon.close();
        m_commCon = null;
        m_thread = null;
    }

    private void _connectSPort() throws Exception {
        m_commCon = (CommConnection) Connector.open("comm:" + _getPort() + CONNECION_STRING, Connector.READ_WRITE, true);
    }

    private String _getPort() {
        String ports = System.getProperty("microedition.commports");
        String port;
        int comma = ports.indexOf(',');
        if (comma > 0) {
            // Parse the first port from the available ports list.
            port = ports.substring(0, comma);
        } else {
            // Only one serial port available.
            port = ports;
        }
        return port;
    }

    private void _sendData(ISendListener sendState) throws Exception {
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            dis = new DataInputStream(m_commCon.openInputStream());
            dos = new DataOutputStream(m_commCon.openOutputStream());
            // Simulated
            //dis = new DataInputStream(new Simulated_SPORT_IN2());
            //dos = new DataOutputStream(new Simulated_SPORT_OUT());

            MenuItem root = null;
            String fullName;
            byte buffer[];

            // Sending menu
            if (!m_wasCanceled) {
                root = getCurrentRootMenu();
                fullName = "Menu Info";
                buffer = getBufferFromMenu(root);
                sendState.setFileName(fullName);

                dis.read();
                dos.writeUTF(fullName);
                _sendFileData(dis, dos, buffer, sendState);
            }

            // Sending files
            if (!m_wasCanceled) {
                Vector files = new Vector();
                getFileItems(root, files);

                int numFiles = files.size();
                dis.read();
                dos.writeInt(numFiles);

                for (int n = 0; n < numFiles && !m_wasCanceled; n++) {
                    MenuItem item = (MenuItem) files.elementAt(n);

                    fullName = item.getFullName();
                    buffer = getFileBuffer(item);
                    sendState.setFileName(fullName);

                    dis.read();
                    dos.writeUTF(fullName);
                    _sendFileData(dis, dos, buffer, sendState);
                }

            }

        } finally {
            try {
                if (dis != null)
                    dis.close();
            } catch (Throwable th) {
            }
            try {
                if (dos != null)
                    dos.close();
            } catch (Throwable th) {
            }
        }
    }

    private void _receiveData(IReceiveListener receiveState) throws Exception {

        DataInputStream dis = null;

        StoreManager.deleteStorages(false);

        try {
            dis = new DataInputStream(m_commCon.openInputStream());
            // Simulated
            //dis = new DataInputStream(new Simulated_SPORT_IN());

            String fileName;
            byte fileData[];
            MenuItem root = null;

            // Receiving menu
            if (!m_wasCanceled) {
                fileName = _receiveFileName(dis);
                receiveState.setFileName(fileName);
                fileData = _receiveFileData(dis, receiveState);
                root = getMenuFromBuffer(fileData);
            }

            // Receiving files
            if (!m_wasCanceled) {
                int numFiles = dis.readInt();
                for (int n = 0; n < numFiles && !m_wasCanceled; n++) {
                    fileName = _receiveFileName(dis);
                    MenuItem fileItem = findFileItem(root, fileName);
                    receiveState.setFileName(fileName);
                    fileData = _receiveFileData(dis, receiveState);
                    if (!m_wasCanceled) {
                        receiveState.startRSUpdate();
                        int index = StoreManager.saveFile(fileData);
                        receiveState.endRSUpdate();
                        fileItem.setFileRecordIndex(index);
                    }
                }

                StoreManager.saveMenu(getBufferFromMenu(root));
            }

        } finally {
            try {
                if (dis != null)
                    dis.close();
            } catch (Throwable th) {
            }
        }

    }

    private void _sendFileData(DataInputStream dis, DataOutputStream dos, byte buffer[], ISendListener sendState) throws Exception {

        int size = buffer.length;

        sendState.setTotalDataSize(size);
        dos.writeInt(size);

        int totalSent = 0;
        final int MARK_SIZE = 1024;
        int needMark = MARK_SIZE;
        while (totalSent < size && !m_wasCanceled) {
            needMark--;
            if(needMark<=0) {
                needMark=MARK_SIZE;
                dis.read();
            }
            dos.writeByte(buffer[totalSent++]);
            sendState.updateSentAmount(totalSent);
        }
    }

    private byte[] _receiveFileData(DataInputStream dis, IReceiveListener receiveState) throws Exception {

        int size = dis.readInt();
        byte buffer[] = new byte[size];

        receiveState.setTotalDataSize(size);

        int totalRead = 0;
        while (totalRead < size && !m_wasCanceled) {
            buffer[totalRead++] = dis.readByte();
            receiveState.updateReceivedAmount(totalRead);
        }

        return buffer;
    }

    private String _receiveFileName(DataInputStream dis) throws Exception {
        String fileName = dis.readUTF();
        return fileName;
    }

    private MenuItem findFileItem(MenuItem root, String fullName) throws Exception {
        try {
            MenuItem mi = root.getSubChild(fullName);
            if (mi == null)
                throw new Exception("Error file item not found: '" + fullName + "'");
            return mi;
        } catch (Throwable th) {
            throw new Exception("Error '" + th.getMessage() + "' while looking for file item '" + fullName + "'");
        }
    }

    private byte[] getBufferFromMenu(MenuItem root) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        root.writeExternal(dos);
        dos.close();
        return baos.toByteArray();
    }

    private MenuItem getMenuFromBuffer(byte buffer[]) throws Exception {
        MenuItem root = MenuItem.createRoot();
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(buffer));
        root.readExternal(dis);
        dis.close();
        return root;
    }

    private MenuItem getCurrentRootMenu() {
        MenuItem currentMenu = (MenuItem) GlobalContext.getData(Constants.GC_MENU_DATA);
        if (currentMenu != null) {
            return currentMenu.getRootParent();
        } else {
            return MenuItem.createRoot();
        }
    }

    private void getFileItems(MenuItem menu, Vector files) {
        for (int n = 0; n < menu.getNumItems(); n++) {
            MenuItem child = menu.getChild(n);
            if (child.isFile()) {
                files.addElement(child);
            } else {
                // skipt first child that pointers to parent menu
                if (child != menu.getParent()) {
                    getFileItems(child, files);
                }
            }
        }
    }

    private byte[] getFileBuffer(MenuItem mi) throws Exception {
        TextData td = TextManager.loadTextData(mi.getAlias(), mi.getFileRecordIndex());
        return td.getStrData().getBytes();
    }

}
