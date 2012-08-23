/**
 * 
 */
package com.jzb.nsf.notes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class NotesCalReader {

    private String  m_dataSourceURL;

    public NotesCalReader() {
    }

    public ArrayList<NotesCalData> getAppointments() throws Exception {

        Connection con = null;
        Statement stm = null;
        ResultSet rs = null;

        try {
            Tracer._info("Reading appointments info from Notes database");

            Tracer._debug("Connecting to Notes database: " + m_dataSourceURL);
            con = DriverManager.getConnection(m_dataSourceURL); // , usr, pswd);
            Tracer._debug("Connection created");

            Tracer._debug("Executing query to retrieve calendar data");
            String strSQL = "SELECT Subject, Body, txtStartDateTime, txtEndDateTime, Location, Repeats, tmpChair FROM Appointment, AppJoinView WHERE Appointment.NoteID=AppJoinView.NoteID";
            stm = con.createStatement();
            rs = stm.executeQuery(strSQL);
            Tracer._debug("Query executed");

            Tracer._debug("Iterating retrieved calendar data");
            ArrayList<NotesCalData> ncdList = new ArrayList<NotesCalData>();
            while (rs.next()) {
                NotesCalData ncd = new NotesCalData();
                ncd.Subject = _getStringValue(rs, 1);
                ncd.Body = _getStringValue(rs, 2);
                ncd.StartDateTime = _getStringValue(rs, 3);
                ncd.EndDateTime = _getStringValue(rs, 4);
                ncd.Location = _getStringValue(rs, 5);
                ncd.Repeats = rs.getString(6) != null;
                ncd.Chair = _getStringValue(rs, 7);
                ncdList.add(ncd);
            }

            Tracer._info("All appointments info have been read");
            return ncdList;

        } catch (Throwable th) {
            // Tracer._error("Error reading appointments info from Notes database", th);
            throw new Exception("Error reading appointments info from Notes database", th);

        } finally {
            _safeClose(rs);
            _safeClose(stm);
            _safeClose(con);
        }
    }

    public void init(String dsName) throws Exception {

        try {
            Tracer._debug("Loading ODBC-JDBC bridge class an native libraries");
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            Tracer._debug("ODBC-JDBC bridge loaded");

            m_dataSourceURL = "jdbc:odbc:" + dsName;

        } catch (Throwable th) {
            // Tracer._error("Error loading ODBC-JDBC bridge class", th);
            throw new Exception("Error loading ODBC-JDBC bridge class", th);
        }

    }

    private String _getStringValue(ResultSet rs, int colIndex, String... nullValue) throws Exception {
        String str = rs.getString(colIndex);
        if (str != null) {
            return str;
        } else {
            return (nullValue == null || nullValue.length <= 0) ? "" : nullValue[0];
        }
    }

    private void _safeClose(Object obj) {
        try {
            if (obj instanceof ResultSet) {
                Tracer._debug("Closing SQL ResultSet");
                ((ResultSet) obj).close();
            } else if (obj instanceof Statement) {
                Tracer._debug("Closing SQL Statement");
                ((Statement) obj).close();
            } else if (obj instanceof Connection) {
                Tracer._debug("Closing SQL Connection");
                ((Connection) obj).close();
            }
        } catch (SQLException ex) {
            Tracer._error("Closing SQL element");
        }
    }

}
