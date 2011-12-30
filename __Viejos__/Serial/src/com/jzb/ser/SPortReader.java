package com.jzb.ser;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;

public class SPortReader {

    public SPortReader() {
    }

    DataInputStream dis;

    public void receive(String basePath, String portCOM) throws Exception {

        File baseFolder = new File(basePath);

        SerialPort sPort = openSPort(portCOM);
        DataInputStream dis = new DataInputStream(sPort.getInputStream());
        DataOutputStream dos = new DataOutputStream(sPort.getOutputStream());

        MenuItem root = receiveMenuInfo(dos, dis, basePath);
        _createFolders(baseFolder, root);

        receiveFiles(baseFolder, dos, dis);

        dis.close();
        dos.close();
    }

    private void receiveFiles(File baseFolder, DataOutputStream dos, DataInputStream dis) throws Exception {

        System.out.println("\n\n***> receiving files\n");
        _sendMark(dos, 'T');
        int numFiles = dis.readInt();
        System.out.println("***> Num files: " + numFiles + "\n\n");
        for (int n = 0; n < numFiles; n++) {
            _sendMark(dos, 'F');
//            byte kk[]=new byte[20];
//            dis.readFully(kk,0,20);
            Thread.sleep(1000);
            String fileName = dis.readUTF();
            System.out.println("***>     Received file Name: " + fileName);
            Thread.sleep(1000);
            byte buffer[] = _receiveData(dos, dis);

            _writeLocalData(baseFolder, fileName, buffer);
        }

    }

    private void _writeLocalData(File baseFolder, String fileName, byte buffer[]) throws Exception {

        System.out.println("***> Writing local file: " + fileName);

        fileName = fileName.replace('.', '/') + ".txt";
        File file = new File(baseFolder, fileName);
        file.getParentFile().mkdirs();

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(buffer);
        bos.close();
    }

    private SerialPort openSPort(String portName) throws Exception {

        waitSPort(portName);

        System.out.println("***> Opening port");
        CommPortIdentifier port = CommPortIdentifier.getPortIdentifier(portName);
        SerialPort sPort = (SerialPort) port.open("Tester", 10000);
        sPort.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_2, SerialPort.PARITY_EVEN);
        sPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);

        return sPort;
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

    private MenuItem receiveMenuInfo(DataOutputStream dos, DataInputStream dis, String basePath) throws Exception {

        System.out.println("\n\n***> receiving Menu Info\n");

        _sendMark(dos, 'M');
        dis.readUTF();
        byte buffer[] = _receiveData(dos, dis);

        MenuItem root = MenuItem.createRoot();
        root.readExternal(new DataInputStream(new ByteArrayInputStream(buffer)));
        return root;
    }

    private byte[] _receiveData(DataOutputStream dos, DataInputStream dis) throws Exception {
        System.out.println("***> receiving data from mobile");

        System.out.println("***>     Receiving Size");
        int size = dis.readInt();
        byte buffer[] = new byte[size];
        System.out.println("***>     Receiving Data (" + size + ")");

        int totalRead = 0;
        final int MARK_SIZE = 1024;
        int needMark = MARK_SIZE;
        while (totalRead < size) {
            needMark--;
            if(needMark<=0) {
                needMark=MARK_SIZE;
                _sendMark(dos,'*');
            }
            int read=dis.read(buffer, totalRead,1);
            totalRead+=read;
        }

        System.out.println("***>     --- DONE ---");

        return buffer;
    }

    private void _sendMark(DataOutputStream dos, char mark) throws Exception {
        Thread.sleep(300);
        dos.write((int) mark);

    }

    private void _createFolders(File baseFolder, MenuItem mi) throws Exception {
        for (int n = 0; n < mi.getNumItems(); n++) {
            MenuItem child = mi.getChild(n);
            if (n > 0 && !child.isFile()) {
                File file = new File(baseFolder, child.getFullName().replace('.', '/'));
                file.mkdirs();
                _createFolders(baseFolder, child);
            }
        }
    }
}
