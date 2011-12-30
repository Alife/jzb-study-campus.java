/**
 * 
 */
package com.isb.patch;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import com.isb.patch.IUpdatingStep.IProgessMonitor;

/**
 * This class is the graphical interface for the user during the updating process. It will show the current step's description that is being executed, the partial step's progress and the overall
 * updating process progress. <br>
 * <br>
 * It implements executes all the Updating Steps and implements IProgressMonitor to allow them to inform about their progress. <br>
 * <br>
 * To execute all the steps a separated thread is created.
 * 
 * @author IS201105
 * 
 */
public class JDUpdater extends JDialog implements Runnable, IProgessMonitor {

    private boolean       m_canceled = false;
    private IUpdatingStep m_currentStep;
    private JLabel        m_lblStepDesc;

    private ILogger       m_logger;
    private JProgressBar  m_pgbStep;
    private JProgressBar  m_pgbTotal;
    private IUpdatingStep m_updSteps[];

    /**
     * Launch the JDialog
     * 
     * @param logger
     *            Logger to be used to store trace info.
     * 
     * @param updSteps
     *            Array of Update Steps to be executed
     * 
     * @throws Exception
     *             If something fails
     */
    public static void showIt(ILogger logger, IUpdatingStep updSteps[]) throws Exception {
        final JDUpdater dialog = new JDUpdater(logger, updSteps);
        dialog.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                dialog.m_canceled = true;
                dialog.dispose();
            }
        });
        new Thread(dialog, "updSteps").start();
        dialog.setVisible(true);
    }

    /**
     * Launch the JDialog
     * 
     * @param logger
     *            Logger to be used to store trace info.
     * 
     * @param updSteps
     *            Array of Update Steps to be executed
     * 
     */
    public JDUpdater(ILogger logger, IUpdatingStep updSteps[]) {
        super();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        m_logger = logger;
        m_updSteps = updSteps;

        setModal(true);
        setResizable(false);
        getContentPane().setLayout(null);
        setTitle("Actualizando / Updating");
        setBounds(100, 100, 393, 212);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (d.getWidth() - getWidth()) / 2, (int) (d.getHeight() - getHeight()) / 2);

        final JLabel lbl1 = new JLabel();
        lbl1.setText("Total:");
        lbl1.setBounds(25, 105, 54, 14);
        getContentPane().add(lbl1);

        final JLabel lbl2 = new JLabel();
        lbl2.setText("Partial:");
        lbl2.setBounds(25, 125, 54, 14);
        getContentPane().add(lbl2);

        m_pgbStep = new JProgressBar();
        m_pgbStep.setStringPainted(true);
        m_pgbStep.setBounds(74, 125, 299, 18);
        getContentPane().add(m_pgbStep);

        m_pgbTotal = new JProgressBar();
        m_pgbTotal.setStringPainted(true);
        m_pgbTotal.setBounds(74, 100, 299, 18);
        getContentPane().add(m_pgbTotal);

        final JButton cancelButton = new JButton();
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(final ActionEvent e) {
                m_canceled = true;
                cancelButton.setEnabled(false);
                if (m_currentStep != null)
                    m_currentStep.canceled();
            }
        });
        cancelButton.setText("Cancel");
        cancelButton.setBounds(280, 157, 93, 23);
        getContentPane().add(cancelButton);

        m_lblStepDesc = new JLabel();
        m_lblStepDesc.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        m_lblStepDesc.setBounds(25, 35, 348, 29);
        getContentPane().add(m_lblStepDesc);

        final JLabel lbl4 = new JLabel();
        lbl4.setFont(new Font("", Font.BOLD, 12));
        lbl4.setText("Paso / Step");
        lbl4.setBounds(15, 15, 93, 14);
        getContentPane().add(lbl4);

        final JLabel lbl5 = new JLabel();
        lbl5.setFont(new Font("", Font.BOLD, 12));
        lbl5.setText("Progreso / Progress");
        lbl5.setBounds(15, 75, 128, 14);
        getContentPane().add(lbl5);
        //
    }

    /**
     * This method will show a MessageBox to the user containing information about the error occurred. Remember that usually just <b>Error Codes</b> will be shown.
     * 
     * @see com.isb.patch.IUpdatingStep.IProgessMonitor#notifyException(com.isb.patch.UpdateException)
     */
    public void notifyException(UpdateException ex) {
        String message = "Codigo del error:\nError code:\n\n" + ex.getCode() + "\n\n";
        JOptionPane.showMessageDialog(new JFrame(), message, "Error ejecutanto / Executing", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Thread "run" method where all the Update Steps will be executed.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {

        boolean wasOK = true;

        try {
            m_logger.debug("Waiting main dialog's window to be shown...");
            while (!m_canceled && !isVisible()) {
                Thread.sleep(10);
            }

            m_pgbTotal.setMaximum(m_updSteps.length);
            m_logger.debug("Starting updating steps execution");
            for (int n = 0; wasOK && !m_canceled && n < m_updSteps.length; n++) {
                m_currentStep = m_updSteps[n];
                m_pgbStep.setValue(0);
                m_lblStepDesc.setText(m_currentStep.getDescription());
                wasOK = m_currentStep.executeStep(this, m_logger);
                m_pgbTotal.setValue(n + 1);
            }
            m_logger.debug("Finished execution");

            if (m_canceled) {
                String message = "Ejecucion abortada\n\nExecution canceled\n\n";
                JOptionPane.showMessageDialog(new JFrame(), message, "Informacion / Information", JOptionPane.INFORMATION_MESSAGE);
            } else if (wasOK) {
                String message = "Ejecucion terminada correctamente\n\nExecution finished correctly\n\n";
                JOptionPane.showMessageDialog(new JFrame(), message, "Informacion / Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Throwable th) {
            m_logger.debug("Error executing updating steps", th);
        }

        dispose();
    }

    /**
     * This method will update the progress bar with the new value
     * 
     * @see com.isb.patch.IUpdatingStep.IProgessMonitor#updateProgress(int)
     */
    public void updateProgress(int newPercentage) {
        m_pgbStep.setValue(newPercentage);
    }

}
