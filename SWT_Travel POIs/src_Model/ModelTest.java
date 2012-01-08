import java.util.ArrayList;
import java.util.HashMap;

import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.srvc.GMapService;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.tpoi.srvc.SyncService;
import com.jzb.util.Des3Encrypter;

/**
 * 
 */

/**
 * @author n63636
 * 
 */
public class ModelTest {

    /**
     * Static Main starting method
     * 
     * @param args
     *            command line parameters
     */
    public static void main(String[] args) {
        try {
            long t1, t2;
            System.out.println("***** TEST STARTED *****");
            ModelTest me = new ModelTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
            System.exit(-1);
        }
    }

    /**
     * Similar to main method but is not static
     * 
     * @param args
     *            command line parameters
     * @throws Exception
     *             if something fails during the execution
     */
    public void doIt(String[] args) throws Exception {

        ModelService.inst._setBaseFolder("C:\\Users\\n63636\\Desktop\\xIPAs\\gmaps");
//        GMapService.inst.login(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

//        ArrayList<TMap> mapsList = GMapService.inst.getUserMapsList();
//        for (TMap map : mapsList) {
//            GMapService.inst.readMapData(map);
//            ModelService.inst.createMap(map);
//        }

        ArrayList<TMap> mapsList = ModelService.inst.getUserMapsList();
        for (TMap map : mapsList) {
            ModelService.inst.readMapData(map);
        }
        for (TMap map : mapsList) {
            System.out.println(map.getAllPoints().size() + " - " +map.getDisplayName());
        }

    }

}
