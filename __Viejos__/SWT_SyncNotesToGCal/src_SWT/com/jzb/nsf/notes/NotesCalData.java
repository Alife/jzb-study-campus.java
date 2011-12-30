/**
 * 
 */
package com.jzb.nsf.notes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * @author n000013
 * 
 */
public class NotesCalData {

    private static SimpleDateFormat s_sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public String                   Body;
    public String                   Chair;
    public String                   EndDateTime;
    public String                   Location;
    public boolean                  Repeats;
    public String                   StartDateTime;
    public String                   Subject;

    public Date getFirstEndDateTime() throws Exception {
        int p = EndDateTime.indexOf(';');
        if (p > 0)
            return s_sdf.parse(EndDateTime.substring(0, p));
        else
            return s_sdf.parse(EndDateTime);
    }

    public Date getFirstStartDateTime() throws Exception {
        int p = StartDateTime.indexOf(';');
        if (p > 0)
            return s_sdf.parse(StartDateTime.substring(0, p));
        else
            return s_sdf.parse(StartDateTime);

    }

    public String getRRule() throws Exception {

        final String dNames[] = { "", "SU", "MO", "TU", "WE", "TH", "FR", "SA" };

        ArrayList<Calendar> eclist = _getRepeatingCalendar(EndDateTime);
        String lastDate = _toICalString(eclist.get(eclist.size() - 1).getTime());

        HashSet<String> days = new HashSet<String>();
        ArrayList<Calendar> sclist = _getRepeatingCalendar(StartDateTime);
        for (Calendar cal : sclist) {
            int n = cal.get(Calendar.DAY_OF_WEEK);
            days.add(dNames[n]);
        }

        String rrule = "RRULE:FREQ=WEEKLY;BYDAY=";
        boolean first = true;
        for (String day : days) {
            if (!first)
                rrule += ",";
            rrule += day;
            first = false;
        }
        rrule += ";UNTIL=" + lastDate + "Z;WKST=MO";

        
        String recurData = "";
        recurData += "DTSTART;TZID=Europe/Madrid:" + _toICalString(getFirstStartDateTime()) + "\r\n";
        recurData += "DTEND;TZID=Europe/Madrid:" + _toICalString(getFirstEndDateTime()) + "\r\n";
        recurData += rrule;

        return recurData;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append("Subject:\t").append(Subject).append("\r\n");
        sb.append("StartDateTime:\t").append(StartDateTime).append("\r\n");
        sb.append("EndDateTime:\t").append(EndDateTime).append("\r\n");
        sb.append("Repeats:\t").append(Repeats).append("\r\n");
        sb.append("Location:\t").append(Location).append("\r\n");
        sb.append("Chair:\t").append(Chair).append("\r\n");
        sb.append("Body:\t").append(Body).append("\r\n");

        return sb.toString();
    }

    /** Appends a zero-padded number to a string builder. */
    private void _appendInt(StringBuilder sb, int num, int numDigits) {

        if (num < 0) {
            sb.append('-');
            num = -num;
        }

        char[] digits = new char[numDigits];
        for (int digit = numDigits - 1; digit >= 0; --digit) {
            digits[digit] = (char) ('0' + num % 10);
            num /= 10;
        }

        sb.append(digits);
    }

    private ArrayList<Calendar> _getRepeatingCalendar(String dates) throws Exception {

        ArrayList<Calendar> rdates = new ArrayList<Calendar>();

        StringTokenizer st = new StringTokenizer(dates, ";");
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            Calendar c = Calendar.getInstance();
            c.setTime(s_sdf.parse(s));
            rdates.add(c);
        }

        return rdates;
    }

    private String _toICalString(Date dt) throws Exception {

        StringBuilder sb = new StringBuilder();

        Calendar dateTime = new GregorianCalendar(TimeZone.getDefault());
        long localTime = dt.getTime();
        dateTime.setTimeInMillis(localTime);

        _appendInt(sb, dateTime.get(Calendar.YEAR), 4);
        _appendInt(sb, dateTime.get(Calendar.MONTH) + 1, 2);
        _appendInt(sb, dateTime.get(Calendar.DAY_OF_MONTH), 2);

        sb.append('T');
        _appendInt(sb, dateTime.get(Calendar.HOUR_OF_DAY), 2);
        _appendInt(sb, dateTime.get(Calendar.MINUTE), 2);
        _appendInt(sb, dateTime.get(Calendar.SECOND), 2);

        //sb.append('Z');

        return sb.toString();

    }
}
