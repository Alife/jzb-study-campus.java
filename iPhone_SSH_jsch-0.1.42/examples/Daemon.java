/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.jcraft.jsch.ChannelForwardedTCPIP;
import com.jcraft.jsch.ForwardedTCPIPDaemon;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class Daemon {

    public static class MyUserInfo implements UserInfo, UIKeyboardInteractive {

        final GridBagConstraints gbc           = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
        String                   passwd;

        JTextField               passwordField = new JPasswordField(20);
        private Container        panel;

        public String getPassphrase() {
            return null;
        }

        public String getPassword() {
            return passwd;
        }

        public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());

            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.gridx = 0;
            panel.add(new JLabel(instruction), gbc);
            gbc.gridy++;

            gbc.gridwidth = GridBagConstraints.RELATIVE;

            JTextField[] texts = new JTextField[prompt.length];
            for (int i = 0; i < prompt.length; i++) {
                gbc.fill = GridBagConstraints.NONE;
                gbc.gridx = 0;
                gbc.weightx = 1;
                panel.add(new JLabel(prompt[i]), gbc);

                gbc.gridx = 1;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.weighty = 1;
                if (echo[i]) {
                    texts[i] = new JTextField(20);
                } else {
                    texts[i] = new JPasswordField(20);
                }
                panel.add(texts[i], gbc);
                gbc.gridy++;
            }

            if (JOptionPane.showConfirmDialog(null, panel, destination + ": " + name, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                String[] response = new String[prompt.length];
                for (int i = 0; i < prompt.length; i++) {
                    response[i] = texts[i].getText();
                }
                return response;
            } else {
                return null; // cancel
            }
        }

        public boolean promptPassphrase(String message) {
            return true;
        }

        public boolean promptPassword(String message) {
            Object[] ob = { passwordField };
            int result = JOptionPane.showConfirmDialog(null, ob, message, JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                passwd = passwordField.getText();
                return true;
            } else {
                return false;
            }
        }

        public boolean promptYesNo(String str) {
            Object[] options = { "yes", "no" };
            int foo = JOptionPane.showOptionDialog(null, str, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            return foo == 0;
        }

        public void showMessage(String message) {
            JOptionPane.showMessageDialog(null, message);
        }
    }

    public static class Parrot implements ForwardedTCPIPDaemon {

        Object[]              arg;
        ChannelForwardedTCPIP channel;
        InputStream           in;
        OutputStream          out;

        public void run() {
            System.out.println("remote port: " + channel.getRemotePort());
            try {
                System.out.println("remote host: " + channel.getSession().getHost());
            } catch (JSchException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            try {
                byte[] buf = new byte[1024];
                while (true) {
                    int i = in.read(buf, 0, buf.length);
                    if (i <= 0)
                        break;
                    out.write(buf, 0, i);
                    out.flush();
                    if (buf[0] == '.')
                        break;
                }
            } catch (IOException e) {
            }
        }

        public void setArg(Object[] arg) {
            this.arg = arg;
        }

        public void setChannel(ChannelForwardedTCPIP c, InputStream in, OutputStream out) {
            this.channel = c;
            this.in = in;
            this.out = out;
        }
    }

    public static void main(String[] arg) {

        int rport;

        try {
            JSch jsch = new JSch();

            String host = null;
            if (arg.length > 0) {
                host = arg[0];
            } else {
                host = JOptionPane.showInputDialog("Enter username@hostname", System.getProperty("user.name") + "@localhost");
            }
            String user = host.substring(0, host.indexOf('@'));
            host = host.substring(host.indexOf('@') + 1);

            Session session = jsch.getSession(user, host, 22);

            String foo = JOptionPane.showInputDialog("Enter remote port number", "8888");
            rport = Integer.parseInt(foo);

            // username and password will be given via UserInfo interface.
            UserInfo ui = new MyUserInfo();
            session.setUserInfo(ui);

            session.connect();

            // session.setPortForwardingR(rport, Parrot.class.getName());
            session.setPortForwardingR(rport, "Daemon$Parrot");
            System.out.println(host + ":" + rport + " <--> " + "Parrot");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}