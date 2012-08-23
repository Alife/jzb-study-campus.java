import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 */

/**
 * @author jzarzuela
 * 
 */
public class Distri {

    public static class TInfo {

        public double dist;
        public double value;

        /**
         * @see java.lang.Object#toString()
         */
        @SuppressWarnings("synthetic-access")
        @Override
        public String toString() {
            String val = "";
            val += "value=" + s_df1.format(value);
            return val;
        }
    }

    private static final DecimalFormat s_df1 = new DecimalFormat("000.00");

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
            Distri me = new Distri();
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

        //@formatter:off
        double values[] = {
                1.0,
                1.7,
                1.8,
                2.0,
                2.1,
                2.4,
                3.5,
                4.7,
                4.9,
                5.0,
                5.1,
                5.2,
                6.5,
                70.7,
                70.9,
                80.0,
                80.1,
                80.3,
                90.0
        };
        //@formatter:on

        // ------------------------------------------------------
        ArrayList<TInfo> infos = new ArrayList<TInfo>();
        for (int n = 0; n < values.length; n++) {
            TInfo info = new TInfo();
            info.value = values[n];
            infos.add(info);
        }

        // ------------------------------------------------------
        _sortAndCalcDist(infos);

        // ------------------------------------------------------
        TreeMap<String, List<TInfo>> groups = new TreeMap<String, List<TInfo>>();
        _splitInGroups(infos, groups, 3);

        for (Map.Entry<String, List<TInfo>> entry : groups.entrySet()) {
            System.out.printf("%s - %s\n", entry.getKey(), entry.getValue());
        }

        // ------------------------------------------------------
        for (TInfo info : infos) {
            // System.out.println(info);
        }

    }

    // ------------------------------------------------------------------------------------------------------------
    private double _calcExcessiveDist(List<TInfo> infos) {

        double avgDist = 0;
        double avgDev = 0;

        for (int n = 0; n < infos.size() - 1; n++) {
            avgDist += infos.get(n).dist;
        }
        avgDist /= infos.size() - 1;

        for (int n = 0; n < infos.size() - 1; n++) {
            avgDev += (infos.get(n).dist - avgDist) * (infos.get(n).dist - avgDist);
        }
        avgDev = Math.sqrt(avgDev / (infos.size() - 1));

        return avgDist + avgDev;
    }

    // ------------------------------------------------------------------------------------------------------------
    private double _getMaxDist(List<TInfo> infos) {

        double maxDist = 0;
        for (int n = 0; n < infos.size() - 1; n++) {
            if (infos.get(n).dist > maxDist) {
                maxDist = infos.get(n).dist;
            }
        }
        return maxDist;
    }

    // ------------------------------------------------------------------------------------------------------------
    private void _sortAndCalcDist(ArrayList<TInfo> infos) {

        Collections.sort(infos, new Comparator<TInfo>() {

            @Override
            public int compare(TInfo o1, TInfo o2) {
                return (int) (o1.value - o2.value);
            }
        });

        for (int n = 0; n < infos.size() - 1; n++) {
            double dist = Math.abs(infos.get(n).value - infos.get(n + 1).value);
            infos.get(n).dist = dist;
        }
    }

    // ------------------------------------------------------------------------------------------------------------
    private void _splitInGroups(List<TInfo> infos, TreeMap<String, List<TInfo>> groups, double minDist) {

        if (infos.size() < 2 || _getMaxDist(infos) <= minDist) {
            return;
        }

        double excessiveDist = _calcExcessiveDist(infos);

        ArrayList<List<TInfo>> groupsFound = new ArrayList<List<TInfo>>();

        int p1 = 0;
        for (int n = 0; n < infos.size() - 1; n++) {
            if (infos.get(n).dist > excessiveDist) {
                String key = "Grp-" + infos.get(p1).value;
                List<TInfo> group = infos.subList(p1, n + 1);
                groupsFound.add(group);
                groups.put(key, group);
                p1 = n + 1;
            }
        }
        if (p1 < infos.size()) {
            String key = "Grp-" + infos.get(p1).value;
            List<TInfo> group = infos.subList(p1, infos.size());
            groupsFound.add(group);
            groups.put(key, group);
        }

        System.out.println();

        if (groupsFound.size() > 1) {
            for (List<TInfo> infoGroup : groupsFound) {
                _splitInGroups(infoGroup, groups, minDist);
            }
        }

    }

}
