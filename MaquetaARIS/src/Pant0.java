import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.swtdesigner.SWTResourceManager;


/**
 * 
 */

/**
 * @author n63636
 *
 */
public class Pant0 {

    private Text m_Cmicrosexportacionesbdp_v1xmlText;
    protected Object result;
    protected Shell  shell;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args) {
        try {
            Pant0 window = new Pant0();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window
     */
    public void open() {
        final Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }

    /**
     * Create contents of the window
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(500, 400);
        shell.setText("Wizard importación µicro");

        final Composite composite = new Composite(shell, SWT.NONE);
        composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        composite.setBounds(0, 0, 494, 64);

        final Label label = new Label(composite, SWT.NONE);
        label.setFont(SWTResourceManager.getFont("", 14, SWT.BOLD));
        label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        label.setText("Selección de fichero de exportación");
        label.setBounds(10, 10, 349, 22);

        final Label label_1 = new Label(composite, SWT.NONE);
        label_1.setBounds(425, 5,77, 53);
        label_1.setBackgroundImage(SWTResourceManager.getImage(Pant0.class, "Icono1.png"));

        final Label label_2 = new Label(composite, SWT.NONE);
        label_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        label_2.setText("µicro: <sin determinar>");
        label_2.setBounds(25, 35, 165, 15);

        final Label seleccioneLasAplicacionesLabel = new Label(shell, SWT.NONE);
        seleccioneLasAplicacionesLabel.setText("Fichero:");
        seleccioneLasAplicacionesLabel.setBounds(10, 85, 56, 15);

        final Group group = new Group(shell, SWT.NONE);
        group.setBounds(0, 305, 525, 82);

        final Button button = new Button(group, SWT.NONE);
        button.setEnabled(false);
        button.setBounds(100, 20,90, 25);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
            }
        });
        button.setText("< Back");

        final Button nextButton = new Button(group, SWT.NONE);
        nextButton.setBounds(192, 20,90, 25);
        nextButton.setText("Next >");

        final Button finishButton = new Button(group, SWT.NONE);
        finishButton.setEnabled(false);
        finishButton.setBounds(292, 20,90, 25);
        finishButton.setText("Finish");

        final Button cancelButton = new Button(group, SWT.NONE);
        cancelButton.setBounds(386, 20,90, 25);
        cancelButton.setText("Cancel");

        final Label label_3 = new Label(group, SWT.NONE);
        label_3.setBackgroundImage(SWTResourceManager.getImage(Pant0.class, "Icono2.png"));
        label_3.setBounds(5, 15, 40, 31);

        final Button buscarButton = new Button(shell, SWT.NONE);
        buscarButton.setText("Buscar...");
        buscarButton.setBounds(378, 80, 90, 25);

        m_Cmicrosexportacionesbdp_v1xmlText = new Text(shell, SWT.BORDER);
        m_Cmicrosexportacionesbdp_v1xmlText.setText("C:\\micros\\exportaciones\\BDP_V1.xml");
        m_Cmicrosexportacionesbdp_v1xmlText.setBounds(70, 80, 302, 25);
        //
    }

}
