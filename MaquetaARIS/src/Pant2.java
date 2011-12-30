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

import com.swtdesigner.SWTResourceManager;


/**
 * 
 */

/**
 * @author n63636
 *
 */
public class Pant2 {

    private Table m_Table;
    protected Object result;
    protected Shell  shell;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args) {
        try {
            Pant2 window = new Pant2();
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
        label.setText("Selección de Servicios");
        label.setBounds(10, 10, 284, 22);

        final Label label_1 = new Label(composite, SWT.NONE);
        label_1.setBounds(425, 5,77, 53);
        label_1.setBackgroundImage(SWTResourceManager.getImage(Pant2.class, "Icono1.png"));

        final Label label_2 = new Label(composite, SWT.NONE);
        label_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        label_2.setText("µicro: BDP V1.1");
        label_2.setBounds(25, 35, 165, 15);

        m_Table = new Table(shell, SWT.BORDER | SWT.CHECK | SWT.MULTI | SWT.FULL_SELECTION);
        m_Table.setLinesVisible(true);
        m_Table.setHeaderVisible(true);
        m_Table.setBounds(10, 100, 464, 159);

        final TableColumn newColumnTableColumn = new TableColumn(m_Table, SWT.NONE);
        newColumnTableColumn.setWidth(100);
        newColumnTableColumn.setText("Sercicio");

        final TableColumn newColumnTableColumn_2 = new TableColumn(m_Table, SWT.NONE);
        newColumnTableColumn_2.setWidth(104);
        newColumnTableColumn_2.setText("Aplicacion");
        
        final TableColumn newColumnTableColumn_1 = new TableColumn(m_Table, SWT.NONE);
        newColumnTableColumn_1.setWidth(224);
        newColumnTableColumn_1.setText("Descripción");

        for(int n=0;n<3;n++) {
            final TableItem item1 = new TableItem(m_Table, SWT.NONE);
            item1.setText(new String[] { "Servicio "+n, "Aplicación 1", "Descripción del servicio "+n });
        }
        
        for(int n=0;n<5;n++) {
            final TableItem item1 = new TableItem(m_Table, SWT.NONE);
            item1.setText(new String[] { "Servicio "+new Character((char)(65+n)), "Aplicación 3", "Descripción del servicio "+new Character((char)(65+n)) });
        }

        final Label seleccioneLasAplicacionesLabel = new Label(shell, SWT.NONE);
        seleccioneLasAplicacionesLabel.setText("Seleccione los servicios a importar:");
        seleccioneLasAplicacionesLabel.setBounds(10, 75, 216, 15);

        final Group group = new Group(shell, SWT.NONE);
        group.setBounds(0, 305, 525, 82);

        final Button button = new Button(group, SWT.NONE);
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
        label_3.setBackgroundImage(SWTResourceManager.getImage(Pant2.class, "Icono2.png"));
        label_3.setBounds(5, 15, 40, 31);

        final Button ubicarEnProyectoButton = new Button(shell, SWT.CHECK);
        ubicarEnProyectoButton.setSelection(true);
        ubicarEnProyectoButton.setText("Crear en proyecto de modelado por defecto (nomenclatura)");
        ubicarEnProyectoButton.setBounds(10, 275, 343, 16);
        //
    }

}
