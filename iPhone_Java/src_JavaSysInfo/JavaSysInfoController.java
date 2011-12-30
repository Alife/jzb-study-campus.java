/*
 * JavaSysInfoController.java
 *
 * Created on 2007/12/22, 10:21
 *
 */
import joc.Message;
import static joc.Static.*;
import obc.NSObject;
import obc.UIPreferencesTable;
import obc.UIPreferencesTextTableCell;
import obc.UITextField;

/**
 * 
 * @author javaflavor (takashi.nishigaya@nifty.com)
 */
public class JavaSysInfoController extends NSObject
{
    static class Prop {
        Prop(String title, String name) {
            this.title = title;
            this.name = name;
        }
        String title;
        String name;
    }
    
    private String groupTitles[] = new String[]{
        "Java Runtime",
        "Java Specification",
        "Java Virtual Machine",
        "GNU Classpath",
        "Operating System"
    };
    
    private Prop sysProps[][] = new Prop[][]{
        // Java Runtime
        {
            new Prop("Version",         "java.version"),
            new Prop("Runtime Version", "java.runtime.version"),
            new Prop("Vendor",          "java.vendor"),
            new Prop("Vendor URL",      "java.vendor.url")
        },
        // Java Specification
        {
            new Prop("Name",            "java.specification.name"),
            new Prop("Version",         "java.specification.version"),
            new Prop("Vendor",          "java.specification.vendor")
        },
        // Java Virtual Machine
        {
            new Prop("Name",            "java.vm.name"),
            new Prop("Version",         "java.vm.version"),
            new Prop("Vendor",          "java.vm.vendor"),
            new Prop("Vendor URL",      "java.vm.vendor.url"),
            new Prop("Specification Name", "java.vm.specification.name"),
            new Prop("Specification Version", "java.vm.specification.version"),
            new Prop("Specification Vendor", "java.vm.specification.vendor")
        },
        // GNU Classpath
        {
            new Prop("VM Short Name",  "gnu.classpath.vm.shortname"),
            new Prop("Version",        "gnu.classpath.version"),
            new Prop("Class Version",  "java.class.version"),
            new Prop("Endian",         "gnu.cpu.endian")
        },
        // Operating System
        {
            new Prop("Name",           "os.name"),
            new Prop("Version",        "os.version"),
            new Prop("Architecture",   "os.arch")
        }
    };

    private UIPreferencesTextTableCell cells[][] =
            new UIPreferencesTextTableCell[groupTitles.length][7];
    
//    public JavaSysInfoController() { this(0); }
//    protected JavaSysInfoController(long address) { super(address); }

    //- (int)numberOfGroupsInPreferencesTable:(id)preferencesTable
    @Message
    public int numberOfGroupsInPreferencesTable$(UIPreferencesTable table) {
//        System.out.println("numberOfGroupsInPreferencesTable$");
        return groupTitles.length;
    }
    
    //- (int)preferencesTable:(id)preferencesTable numberOfRowsInGroup:(int)group
    @Message
    public int preferencesTable$numberOfRowsInGroup$(UIPreferencesTable table, int group) {
//        System.out.println("preferencesTable$numberOfRowsInGroup$ group="+group+" -> "+rowTitles[group].length);
        return sysProps[group].length;
    }
    
    //- (id)preferencesTable:(id)preferencesTable cellForRow:(int)row inGroup:(int)group
    @Message
    public Object preferencesTable$cellForRow$inGroup$(UIPreferencesTable table, int row, int group) {
//        System.out.println("preferencesTable$cellForRow$inGroup$ row="+row+", group="+group);
        return cells[group][row];
    }
    
    //- (id)preferencesTable:(id)preferencesTable titleForGroup:(int)group
    @Message
    public Object preferencesTable$titleForGroup$(UIPreferencesTable table, int group) {
//        System.out.println("preferencesTable$titleForGroup$ group="+group);
        return groupTitles[group];
    }
    
    //- (float)preferencesTable:(id)preferencesTable heightForRow:(int)row
    //inGroup:(int)group withProposedHeight:(float)proposedHeight
    @Message
    public float preferencesTable$heightForRow$inGroup$withProposedHeight$
            (UIPreferencesTable table, int row, int group, float proposedHeight) {
        return 48;
    }
    
    @Message @Override
    public JavaSysInfoController init() {
//        System.out.println("JavaSysInfoController#init()");
        super.init();
        // setup UIPreferecesTextTableCells
        for (int group = 0; group < sysProps.length; group++) {
            for (int row = 0; row < sysProps[group].length; row++) {
                cells[group][row] = (UIPreferencesTextTableCell)
                    new UIPreferencesTextTableCell().init();
                cells[group][row].setTitle$(sysProps[group][row].title);
                cells[group][row].setValue$(
                        System.getProperty(sysProps[group][row].name, "N/A"));
                UITextField field = (UITextField)cells[group][row].textField();
                // not editable
                field.setEnabled$(NO);
            }
        }
        return this;
    }
}
