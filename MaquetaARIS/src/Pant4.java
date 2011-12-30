import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.swtdesigner.SWTResourceManager;


/**
 * 
 */

/**
 * @author n63636
 *
 */
public class Pant4 {

    private Tree tree;
    protected Object result;
    protected Shell  shell;

    /**
     * Launch the application
     * @param args
     */
    public static void main(String[] args) {
        try {
            Pant4 window = new Pant4();
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
        label.setText("Selección de Operaciones");
        label.setBounds(10, 10, 284, 22);

        final Label label_1 = new Label(composite, SWT.NONE);
        label_1.setBounds(425, 5,77, 53);
        label_1.setBackgroundImage(SWTResourceManager.getImage(Pant4.class, "Icono1.png"));

        final Label label_2 = new Label(composite, SWT.NONE);
        label_2.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        label_2.setText("µicro: BDP V1.1");
        label_2.setBounds(25, 35, 165, 15);

        final Label seleccioneLasAplicacionesLabel = new Label(shell, SWT.NONE);
        seleccioneLasAplicacionesLabel.setText("Seleccione las operaciones a importar:");
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
        nextButton.setEnabled(false);
        nextButton.setBounds(192, 20,90, 25);
        nextButton.setText("Next >");

        final Button finishButton = new Button(group, SWT.NONE);
        finishButton.setBounds(292, 20,90, 25);
        finishButton.setText("Finish");

        final Button cancelButton = new Button(group, SWT.NONE);
        cancelButton.setBounds(386, 20,90, 25);
        cancelButton.setText("Cancel");

        final Label label_3 = new Label(group, SWT.NONE);
        label_3.setBackgroundImage(SWTResourceManager.getImage(Pant4.class, "Icono2.png"));
        label_3.setBounds(5, 15, 40, 31);

        tree = new Tree(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        tree.setBounds(10, 96, 464, 200);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        
        TreeItem treeItem0 = new TreeItem(tree, 0);
        treeItem0.setText("Aplicación 1");
        TreeItem treeItem3 = new TreeItem(tree, 0);
        treeItem3.setText("Aplicación 3");
        
        for(int n=0;n<3;n++) {
            TreeItem treeItem = new TreeItem(treeItem0, 0);
            treeItem.setText("Servicio "+n);
            for(int m=0;m<3;m++) {
                TreeItem treeItemx = new TreeItem(treeItem, 0);
                treeItemx.setText("Operación @"+n);
            }
        }
        
        for(int n=0;n<3;n++) {
            TreeItem treeItem = new TreeItem(treeItem3, 0);
            treeItem.setText("Servicio "+new Character((char)(65+n)));
            for(int m=0;m<3;m++) {
                TreeItem treeItemx = new TreeItem(treeItem, 0);
                treeItemx.setText("Operación @"+new Character((char)(65+m)));
            }
        }
        
        /*
        for (int loopIndex0 = 0; loopIndex0 < 3; loopIndex0++) {
            TreeItem treeItem0 = new TreeItem(tree, 0);
            treeItem0.setText("Aplicación " + loopIndex0);
            for (int loopIndex1 = 0; loopIndex1 < 10; loopIndex1++) {
              TreeItem treeItem1 = new TreeItem(treeItem0, 0);
              treeItem1.setText("Level 1 Item " + loopIndex1);
              for (int loopIndex2 = 0; loopIndex2 < 10; loopIndex2++) {
                TreeItem treeItem2 = new TreeItem(treeItem1, 0);
                treeItem2.setText("Level 2 Item " + loopIndex2);
              }
            }
          }
          */        
        //
    }

}
