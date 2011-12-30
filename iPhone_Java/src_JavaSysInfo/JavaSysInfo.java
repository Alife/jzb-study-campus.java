/*
 * JavaSysInfo.java
 *
 * Created on 2007/12/22, 10:21
 *
 */
import joc.Message;
import static joc.Static.*;
import obc.CGRect;
import obc.CGSize;
import obc.UIApplication;
import obc.UIHardware;
import obc.UINavigationBar;
import obc.UINavigationItem;
import obc.UIPreferencesTable;
import obc.UIView;
import obc.UIWindow;

/**
 * 
 * @author javaflavor (takashi.nishigaya@nifty.com)
 */
public class JavaSysInfo extends UIApplication
{
//    public JavaSysInfo() { this(0); }
//    protected JavaSysInfo(long address) { super(address); }

    @Message
    public void applicationDidFinishLaunching$(Object unused) {
        // CGRect
        CGRect rect = UIHardware.fullScreenApplicationContentRect();
        rect.origin.x = rect.origin.y = 0;
        CGSize size = rect.size;
        CGSize sizenavbar = UINavigationBar.defaultSize();
        float navbarheight = sizenavbar.height;
        CGRect rectnavbar = new CGRect(0, 0, size.width, navbarheight);
        CGRect rectview = new CGRect(0, navbarheight,
                size.width, size.height-navbarheight);

        // UIWindow & UIView
        UIWindow window = new UIWindow().initWithContentRect$(rect);
        UIView view = new UIView().initWithFrame$(rect);
        window.setContentView$(view);

        // UINavigationBar
        UINavigationBar navbar = new UINavigationBar()
            .initWithFrame$(rectnavbar);
        UINavigationItem navitem = new UINavigationItem()
            .initWithTitle$("JavaSysInfo");
        navbar.pushNavigationItem$(navitem);
//        navbar.showButtonsWithLeftTitle$rightTitle$(null, "About");
//        navbar.setDelegate$(new NavBarController().init());
        // 	navbar.setBarStyle$(0);

        // UIPreferencesTable
        UIPreferencesTable prefs = new UIPreferencesTable()
            .initWithFrame$(rectview);
        // Controller
        JavaSysInfoController controller = new JavaSysInfoController().init();
        prefs.setDataSource$(controller);
        prefs.setDelegate$(controller);
        prefs.reloadData();
        
        // setup view
        view.addSubview$(navbar);
        view.addSubview$(prefs);

        // show window
        window.orderFront$(this);
        window.makeKey$(this);
        window._setHidden$(NO);
    }
    
//    public static void main(String[] args) {
//        Class mainclass = JavaSysInfo.class;
//
//        NSAutoreleasePool pool =
//                (NSAutoreleasePool) new NSAutoreleasePool().init();
//
//        ArrayList<Pointer<Byte>> argl = new ArrayList<Pointer<Byte>>(args.length + 2);
//        argl.add(box(mainclass.getName()));
//        for (String arg : args)
//            argl.add(box(arg));
//        argl.add(new Pointer<Byte>());
//        Pointer<Pointer<Byte>> argv = box(argl);
//        
//        C.UIApplicationMain(argl.size()-1, argv, mainclass);
//        
//        for (Pointer arg : argl)
//            arg.free();
//        argv.free();
//    }

}
