import java.util.ArrayList;

import com.jzb.tpoi.data.NMCollection;
import com.jzb.tpoi.data.TCategory;
import com.jzb.tpoi.data.TMap;
import com.jzb.tpoi.data.TPoint;
import com.jzb.tpoi.srvc.GMapService;
import com.jzb.tpoi.srvc.ModelService;
import com.jzb.tpoi.srvc.SyncService;
import com.jzb.util.DefaultHttpProxy;
import com.jzb.util.Des3Encrypter;
import com.jzb.util.Tracer;

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
            Tracer._debug("***** TEST STARTED *****");
            ModelTest me = new ModelTest();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            Tracer._debug("***** TEST FINISHED [" + (t2 - t1) + "]*****");
            System.exit(1);
        } catch (Throwable th) {
            Tracer._error("***** TEST FAILED *****");
            Tracer._error("Exception: ", th);
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

        DefaultHttpProxy.setDefaultProxy();

        ModelService.inst._setBaseFolder("C:\\Users\\n63636\\Desktop\\xIPAs\\gmaps");
        GMapService.inst.login(Des3Encrypter.decryptStr("PjN1Jb0t6CYNTbO/xEgJIjCPPPfsmPez"), Des3Encrypter.decryptStr("8ivdMeBQiyQtSs1BFkf+mw=="));

//        ArrayList<TMap> mapsList = ModelService.inst.getUserMapsList(false);
//        for (TMap map : mapsList) {
//            ModelService.inst.readMapData(map);
//            for (TCategory cat : map.getCategories()) {
//                NMCollection<TPoint> lstPoints = cat.getPoints();
//                for (TPoint point : map.getPoints()) {
//                    lstPoints.add(point);
//                    point.touchAsUpdated();
//                }
//                cat.touchAsUpdated();
//            }
//            ModelService.inst.updateMap(map);
//        }

         SyncService.inst.syncAllMaps();

    }

}
