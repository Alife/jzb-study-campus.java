package com.jzb.ser;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

public class SPortWriter {

    public SPortWriter() {
    }

    DataInputStream dis;

    public void send(String basePath, String portCOM) throws Exception {

        MenuItem root = readMenuInfo(basePath);

        DataOutputStream dos = openSPort(portCOM);
        // Simulado
        //DataOutputStream dos = new DataOutputStream(new java.io.FileOutputStream("D:\\WKSPs\\Consolidado\\Booklet\\res\\data\\bin.data"));

        sendMenuInfo(dos, root);

        System.out.println("\n\n***> sending files\n");
        ArrayList files = new ArrayList();
        getJustFiles(root, files);
        sendFiles(basePath, dos, files);

        dos.close();
    }

    private void getJustFiles(MenuItem root, ArrayList items) {
        int len = root.getNumItems();
        for (int n = 0; n < len; n++) {
            MenuItem mi = root.getChild(n);
            if (!mi.isFile()) {
                if (mi != root.getParent())
                    getJustFiles(mi, items);
            } else {
                items.add(mi);
            }
        }
    }

    private void sendFiles(String basePath, DataOutputStream dos, ArrayList files) throws Exception {

        dos.writeInt(files.size());

        for (int n = 0; n < files.size(); n++) {
            MenuItem mi = (MenuItem) files.get(n);
            String fn = mi.getFullName().replace(".", "/") + ".txt";
            byte buffer[] = readLocalData(basePath + fn);
            sendData(dos, mi.getFullName(), buffer);
        }

    }

    private void sendMenuInfo(DataOutputStream dos, MenuItem root) throws Exception {

        System.out.println("***> sending menu");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos2 = new DataOutputStream(baos);
        root.writeExternal(dos2);
        dos2.close();

        sendData(dos, "Menu Info", baos.toByteArray());
    }

    private byte[] readLocalData(String fileName) throws Exception {

        System.out.println("***> Reading local file: " + fileName);

        byte buffer[] = new byte[1024];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (bis.available() > 0) {
            int len = bis.read(buffer);
            baos.write(buffer, 0, len);
        }
        bis.close();

        return baos.toByteArray();
    }

    private DataOutputStream openSPort(String portName) throws Exception {

        waitSPort(portName);

        System.out.println("***> Opening port");
        CommPortIdentifier port = CommPortIdentifier.getPortIdentifier(portName);
        SerialPort sPort = (SerialPort) port.open("Tester", 10000);
        sPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_EVEN);
        sPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
        OutputStream os = sPort.getOutputStream();
        return new DataOutputStream(os);
    }

    private void waitSPort(String portName) throws Exception {

        System.out.print("***> Waiting port [" + portName + "]:");

        for (;;) {
            Enumeration portList = CommPortIdentifier.getPortIdentifiers();
            while (portList.hasMoreElements()) {
                CommPortIdentifier cpi = (CommPortIdentifier) portList.nextElement();
                if (cpi.getName().equals(portName))
                    return;
            }
            System.out.print('.');
            Thread.sleep(500);
        }
    }

    private MenuItem readMenuInfo(String basePath) throws Exception {
        System.out.println("***> Reading menu info from: " + basePath);
        File rootPath = new File(basePath);
        MenuItem rootMenu = MenuItem.createRoot();
        readMenu2(rootPath, rootMenu);
        return rootMenu;
    }

    private void readMenu2(File folder, MenuItem parentMenu) throws Exception {
        File files[] = folder.listFiles();
        for (int n = 0; n < files.length; n++) {
            if (hasCorrectExtension(files[n])) {
                MenuItem mi;
                String alias = getNameWithoutExtension(files[n]);
                mi = new MenuItem(alias, !files[n].isDirectory());
                parentMenu.addChild(mi);
                if (files[n].isDirectory()) {
                    readMenu2(files[n], mi);
                }
            }
        }
    }

    private String getNameWithoutExtension(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf('.');
        if (pos != -1)
            return name.substring(0, pos);
        else
            return name;
    }

    private boolean hasCorrectExtension(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf('.');
        if (pos == -1)
            return true;
        else
            return name.substring(pos).equalsIgnoreCase(".txt");
    }

    private void sendData(DataOutputStream dos, String name, byte buffer[]) throws Exception {
        System.out.println("***> sending data to mobile");

        System.out.println("***>     Sending Name");
        dos.writeUTF(name);
        System.out.println("***>     Sending Size");
        dos.writeInt(buffer.length);
        System.out.println("***>     Sending Data");

        final int CHUNCK_SIZE = 100;
        int chunks = buffer.length / CHUNCK_SIZE;
        int pos = 0;
        for (int n = 0; n < chunks; n++) {
            dos.write(buffer, pos, CHUNCK_SIZE);
            pos += CHUNCK_SIZE;
        }
        dos.write(buffer, pos, buffer.length - pos);
        System.out.println("***>     --- DONE ---");
    }
}
