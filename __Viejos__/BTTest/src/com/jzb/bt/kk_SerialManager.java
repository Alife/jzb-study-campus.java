package com.jzb.bt;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;

public class kk_SerialManager implements Runnable {

    private CommConnection m_commCon;
    private BTMidlet       m_owner;
    private StringBuffer   m_sb = new StringBuffer();

    public kk_SerialManager(BTMidlet owner) {
        m_owner = owner;
    }

    private void openPort() throws Throwable {

        String ports = System.getProperty("microedition.commports");
        String port;

        System.out.println(ports);

        int comma = ports.indexOf(',');
        if (comma > 0) {
            // Parse the first port from the available ports list.
            port = ports.substring(0, comma);
        } else {
            // Only one serial port available.
            port = ports;
        }

        System.out.println(port);

        showTextMessage("Opening port: " + port);
        m_commCon = (CommConnection) Connector.open("comm:" + port, Connector.READ_WRITE, true);
        showTextMessage("Port openned");
    }

    private void closePort() throws Throwable {
        showTextMessage("Closing port");
        m_commCon.close();
        showTextMessage("Port closed");
    }

    private String readData() throws Throwable {
        showTextMessage("Requesting IS");
        DataInputStream dis = m_commCon.openDataInputStream();
        showTextMessage("IS got");

        showTextMessage("Waiting for first byte...");
        int n = 0;
        while (dis.available() <= 0) {
            Thread.sleep(100);
            n++;
            showTextMessageNCR("+");
        }

        showTextMessage("Reading UTF: " + dis.available());
        String cad = "";
        while (dis.available() > 0) {
            showTextMessageNCR("#");
            char c = dis.readChar();
            showTextMessageNCR("" + c);
            cad += c;
        }
        // String cad = dis.readUTF();

        showTextMessage("Closing IS");
        dis.close();

        return cad;
    }

    private void writeData(String txt) throws Throwable {
        showTextMessage("Requesting OS");
        DataOutputStream dos = m_commCon.openDataOutputStream();
        showTextMessage("OS got");

        showTextMessage("Writing UTF");
        dos.writeUTF(txt);

        showTextMessage("Closing OS");
        dos.close();
    }

    private void showTextMessage(String text) {
        m_sb.append(text + "\n");
        m_owner.showTextMessage(m_sb.toString());
    }

    private void showTextMessageNCR(String text) {
        m_sb.append(text);
        m_owner.showTextMessage(m_sb.toString());
    }

    public void run() {
        try {

            openPort();

            String txt = readData();
            showTextMessage("Received: " + txt);

            writeData(txt + "***");

            closePort();

        } catch (Throwable e) {
            e.printStackTrace();
            m_owner.showErrorMessage(e);
        }

    }
}
