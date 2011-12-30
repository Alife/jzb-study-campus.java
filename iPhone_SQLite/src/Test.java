import java.io.FileOutputStream;
import java.sql.*;

/**
 * @author n63636
 * 
 */
public class Test {

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
            Test me = new Test();
            t1 = System.currentTimeMillis();
            me.doIt(args);
            t2 = System.currentTimeMillis();
            System.out.println("***** TEST FINISHED [" + (t2 - t1) + "]*****");
        } catch (Throwable th) {
            System.out.println("***** TEST FAILED *****");
            th.printStackTrace(System.out);
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
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\n63636\\Desktop\\Nueva carpeta\\HomeDomain\\Library\\AddressBook\\AddressBookImages.sqlitedb");

        PreparedStatement prep = conn.prepareStatement("Select record_id,crop_x,crop_y,crop_width,data from ABFullSizeImage");
        ResultSet rs = prep.executeQuery();
        while (rs.next()) {
            System.out.println("record_id " + rs.getObject(1));
            System.out.println("crop_x " + rs.getObject(2));
            System.out.println("crop_y " + rs.getObject(3));
            System.out.println("crop_width " + rs.getObject(4));
            System.out.println("data ");
            byte[] data = (byte[]) rs.getObject(5);
            for (byte b : data) {
                System.out.print(" " + b);
            }
            System.out.println();

            _saveData((Integer) rs.getObject(1), data);
        }
        rs.close();
        prep.close();
        conn.close();
    }

    private void _saveData(int id, byte[] data) throws Exception {
        FileOutputStream fos = new FileOutputStream("C:\\Users\\n63636\\Desktop\\Nueva carpeta\\HomeDomain\\Library\\AddressBook\\data_" + id + ".jpg");
        fos.write(data);
        fos.close();
    }

    public static void doIt2(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        Statement stat = conn.createStatement();
        stat.executeUpdate("drop table if exists people;");
        stat.executeUpdate("create table people (name, occupation);");
        PreparedStatement prep = conn.prepareStatement("insert into people values (?, ?);");

        prep.setString(1, "Gandhi");
        prep.setString(2, "politics");
        prep.addBatch();
        prep.setString(1, "Turing");
        prep.setString(2, "computers");
        prep.addBatch();
        prep.setString(1, "Wittgenstein");
        prep.setString(2, "smartypants");
        prep.addBatch();

        conn.setAutoCommit(false);
        prep.executeBatch();
        conn.setAutoCommit(true);

        ResultSet rs = stat.executeQuery("select * from people;");
        while (rs.next()) {
            System.out.println("name = " + rs.getString("name"));
            System.out.println("job = " + rs.getString("occupation"));
        }
        rs.close();
        conn.close();
    }
}
