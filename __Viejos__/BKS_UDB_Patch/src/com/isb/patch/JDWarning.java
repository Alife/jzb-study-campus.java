/**
 * 
 */
package com.isb.patch;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

/**
 * Swing JDialog that will show a Warning message to the user. They will have the chance of accepting or canceling the execution.
 * 
 * @author IS201105
 * 
 */
public class JDWarning extends JDialog {

    private boolean m_wasAccepted;

    /**
     * Launch the JDialog
     * 
     * @param message
     *            Information to be shown to the user
     *            
     * @return TRUE if was accepted and FALSE if it was canceled
     * 
     * @throws Exception
     */
    public static boolean showIt(String message) throws Exception {
        final JDWarning me = new JDWarning(message);
        me.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                me.m_wasAccepted = false;
                me.dispose();
            }
        });
        me.setVisible(true);
        return me.m_wasAccepted;
    }

    /**
     * Create the JDialog
     * 
     * @param message
     *            Information to be shown to the user
     */
    public JDWarning(String message) {
        super();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        setTitle("Aviso / Warning");
        setResizable(false);
        setModal(true);
        setBounds(100, 100, 700, 487);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (d.getWidth() - getWidth()) / 2, (int) (d.getHeight() - getHeight()) / 2);

        final JButton okButton = new JButton();
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_wasAccepted = true;
                dispose();
            }
        });
        okButton.setText("Ok");
        okButton.setBounds(485, 430, 93, 23);
        getContentPane().add(okButton);

        final JButton cancelButton = new JButton();
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_wasAccepted = false;
                dispose();
            }
        });
        cancelButton.setText("Cancel");
        cancelButton.setBounds(590, 430, 93, 23);
        getContentPane().add(cancelButton);

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 0, 694, 417);
        getContentPane().add(scrollPane);

        final JEditorPane textPane = new JEditorPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setText(message);
        textPane.setBounds(0, 0, 694, 417);
        scrollPane.setViewportView(textPane);
        //
    }

}
