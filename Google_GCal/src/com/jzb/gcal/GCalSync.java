/**
 * 
 */
package com.jzb.gcal;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.VCalendar;
import net.fortuna.ical4j.model.component.VEvent;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.EventFeed;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.data.extensions.Reminder.Method;

/**
 * @author n000013
 * 
 */
public class GCalSync {

    private static final int                      CMD_DELETEALL       = 3;
    private static final int                      CMD_ENCODE_USER_PWD = 2;
    private static final HashMap<String, Integer> CMD_IDS             = new HashMap<String, Integer>();
    private static final int                      CMD_UPDATE          = 1;
    private static final String                   EPROP_NOTESID       = "NotesID";
    private static final TimeZone                 MY_TZ               = TimeZone.getDefault();

    private static final int                      NUM_RETRIES         = 5;

    private static final SimpleDateFormat         trz_sdf             = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    static {
        CMD_IDS.put("UPDATE", CMD_UPDATE);
        CMD_IDS.put("ENCODE_USER_PWD", CMD_ENCODE_USER_PWD);
        CMD_IDS.put("DELETEALL", CMD_DELETEALL);
    }
    // ArrayList<CalendarEventEntry> m_allGCalEntries = new ArrayList<CalendarEventEntry>();
    private URL                                   m_eventFeedUrl;

    private CalendarService                       m_myService;

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
            GCalSync me = new GCalSync();
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

        // Reads properties
        _trace("Reading properties '/GCalSync.properties'");
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/GCalSync.properties"));

        // Initializes Google connection
        _trace("Initializing Google_Calendar service");
        initGoogle(props);

        // Acts depending on CMD
        String cmdStr = props.getProperty("cmd", "");
        switch (CMD_IDS.get(cmdStr)) {

            case CMD_UPDATE:
                cmd_updateCalendar(props);
                break;

            case CMD_ENCODE_USER_PWD:
                cmd_encodeUserPwd(props);
                break;

            case CMD_DELETEALL:
                cmd_deleteAllCurrentEvents(props);
                break;

            default:
                _trace("Error: Unknown command: " + cmdStr);
        }

    }

    private String _calcRRule(String rdate) {

        final String dNames[] = { "", "SU", "MO", "TU", "WE", "TH", "FR", "SA" };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmSS'Z'");
        Calendar cal = Calendar.getInstance();
        String strDate = "";
        HashSet<String> days = new HashSet<String>();

        StringTokenizer st = new StringTokenizer(rdate, ",");
        while (st.hasMoreTokens()) {
            strDate = st.nextToken();
            Date d;
            try {
                d = sdf.parse(strDate);
                cal.setTime(d);
                int n = cal.get(Calendar.DAY_OF_WEEK);
                days.add(dNames[n]);
            } catch (Exception ex) {
                _trace("Error parsing recurrence date: " + strDate);
                return "";
            }
        }

        String rrule = "RRULE:FREQ=WEEKLY;BYDAY=";
        boolean first = true;
        for (String day : days) {
            if (!first)
                rrule += ",";
            rrule += day;
            first = false;
        }
        rrule += ";UNTIL=" + strDate + ";WKST=MO";

        return rrule;
    }

    private Date _getCEEDate(CalendarEventEntry cee) {
        List<When> lWhen = cee.getTimes();
        if (lWhen != null && lWhen.size() > 0) {
            Date d = new Date(lWhen.get(0).getEndTime().getValue());
            return d;
        } else {
            return null;
        }
    }

    private String _getClearText(String cad) {

        String msg = "";

        int n = 0;
        while (n < cad.length()) {
            String h = "0x";
            h += cad.charAt(n++);
            h += cad.charAt(n++);
            int i = (Integer.decode(h).intValue() ^ 0x00000055) & 0x000000FF;
            char c = (char) i;
            msg += c;
        }

        return msg;
    }

    private String _getEncodedText(String cad) {

        String msg = "";
        int n = 0;
        while (n < cad.length()) {
            char c = cad.charAt(n++);
            int i = (((int) c) & 0x000000FF) ^ 0x00000055;
            msg += Integer.toHexString(i);
        }
        return msg;

    }

    private Date _getLastMonthDate() {

        Calendar lastMonth = Calendar.getInstance();

        lastMonth.set(Calendar.HOUR_OF_DAY, 0);
        lastMonth.set(Calendar.MINUTE, 0);
        lastMonth.set(Calendar.SECOND, 0);
        lastMonth.set(Calendar.MILLISECOND, 0);

        lastMonth.set(Calendar.DATE, 20);
        lastMonth.roll(Calendar.MONTH, false);

        Date oldDate = lastMonth.getTime();
        return oldDate;

    }

    private String _getNotesID(CalendarEventEntry entry) {

        for (ExtendedProperty eprop : entry.getExtendedProperty()) {
            if (eprop.getName().equals(EPROP_NOTESID))
                return eprop.getValue();
        }
        return null;

    }

    private Date _getOldDate() {

        Calendar lastMonth = Calendar.getInstance();

        lastMonth.set(Calendar.HOUR_OF_DAY, 0);
        lastMonth.set(Calendar.MINUTE, 0);
        lastMonth.set(Calendar.SECOND, 0);
        lastMonth.set(Calendar.MILLISECOND, 0);

        lastMonth.set(Calendar.DAY_OF_MONTH, 1);

        Date oldDate = lastMonth.getTime();
        return oldDate;

    }

    private String _getStrValue(VEvent iCalEv, String propName) {
        Property prop = iCalEv.getProperty(propName);
        if (prop != null)
            return prop.getValue();
        else
            return "";
    }

    private String _getWhenDate(CalendarEventEntry gEv) {
        List<When> l = gEv.getTimes();
        if (l != null && l.size() > 0) {
            return l.get(0).getStartTime().toUiString();
        } else {
            return "-NT-";
        }
    }

    private void _gSrvc_delete(URL entryUrl) throws Exception {
        Exception ex = null;
        for (int n = 0; n < NUM_RETRIES; n++) {
            try {
                m_myService.delete(entryUrl);
                return;
            } catch (Exception e) {
                ex = e;
            }
            _trace("Retrying. Error caught: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
        throw ex;
    }

    private <F extends BaseFeed<?, ?>> F _gSrvc_getFeed(URL feedUrl, Class<F> feedClass) throws Exception {
        Exception ex = null;
        for (int n = 0; n < NUM_RETRIES; n++) {
            try {
                return m_myService.getFeed(feedUrl, feedClass);
            } catch (Exception e) {
                ex = e;
            }
            _trace("Retrying. Error caught: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
        throw ex;
    }

    private <E extends BaseEntry<?>> E _gSrvc_insert(URL feedUrl, E entry) throws Exception {
        Exception ex = null;
        for (int n = 0; n < NUM_RETRIES; n++) {
            try {
                return m_myService.insert(feedUrl, entry);
            } catch (Exception e) {
                ex = e;
            }
            _trace("Retrying. Error caught: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
        throw ex;
    }

    private <E extends BaseEntry<?>> E _gSrvc_update(URL entryUrl, E entry) throws Exception {
        Exception ex = null;
        for (int n = 0; n < NUM_RETRIES; n++) {
            try {
                return m_myService.update(entryUrl, entry);
            } catch (Exception e) {
                ex = e;
            }
            _trace("Retrying. Error caught: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
        throw ex;
    }

    private void _trace(String msg) {
        System.out.println("* TRZ[" + trz_sdf.format(new Date()) + "] *" + "> " + msg);
    }

    private void cmd_deleteAllCurrentEvents(Properties props) throws Exception {

        _trace("Deleting ALL ITEMS!!!");
        for (;;) {

            CalendarEventFeed cef = _gSrvc_getFeed(m_eventFeedUrl, CalendarEventFeed.class);
            for (CalendarEventEntry cee : cef.getEntries()) {
                _gSrvc_delete(new URL(cee.getEditLink().getHref()));
            }

            if (cef.getNextLink() == null)
                break;
        }
    }

    private void cmd_encodeUserPwd(Properties props) throws Exception {
        _trace("Enconding the user and password to be used in properties file");
        System.out.println("user = " + _getEncodedText(props.getProperty("user")));
        System.out.println("pwd = " + _getEncodedText(props.getProperty("pwd")));
    }

    private void cmd_updateCalendar(Properties props) throws Exception {

        // Reads current event entries with NotesIDs
        _trace("Reading current Notes events stored in Google_Calendar");
        HashMap<String, CalendarEventEntry> curNotesEv = getCurrentNotesEvents();

        // Reads the iCal file
        _trace("Reading exported iCal file");
        VCalendar ical = readICalFile(props);

        ArrayList<VEvent> icalEvList = new ArrayList<VEvent>();

        _trace("Filtering obsolete entries from those read from ical File");
        Date oldDate = _getOldDate();
        for (Component c : ical.getComponents()) {

            // Just processes VEvent entries
            if (!(c instanceof VEvent))
                continue;

            VEvent e = (VEvent) c;

            // add it to be processed if it is new or repetitive
            if (e.getProperty(Property.RDATE) != null || e.getStartDate().getDate().after(oldDate)) {
                icalEvList.add(e);
            }
        }

        // Iterates the entries read from iCal file
        _trace("Iterating elements from exported iCal file");
        for (VEvent cEv : icalEvList) {

            // Creates the equivalent Google Event
            CalendarEventEntry gEntry = createGoogleEventEntry(cEv, curNotesEv);

            // Insert or update the google calendar
            if (gEntry.getId() == null) {
                _trace("* Inserting new event: [" + _getWhenDate(gEntry) + "] " + gEntry.getTitle().getPlainText());
                CalendarEventEntry newEvEntry = _gSrvc_insert(m_eventFeedUrl, gEntry);
                // _trace(_getNotesID(newEvEntry));
            } else {
                _trace("+ Updating event: [" + _getWhenDate(gEntry) + "] " + gEntry.getTitle().getPlainText());
                CalendarEventEntry newEvEntry = _gSrvc_update(new URL(gEntry.getEditLink().getHref()), gEntry);
                // _trace(_getNotesID(newEvEntry));
            }
        }

        // En este punto, lo que quede en "curNotesEv" es que no se ha actualizado
        // Bien por "viejo" o por "nuevo" con respecto al fichero iCal leido
        _trace("Deleting old entries in Google Calendar");
        Date lastMonth = _getLastMonthDate();
        for (CalendarEventEntry cee : curNotesEv.values()) {
            Date d = _getCEEDate(cee);
            if (cee.getRecurrence() == null && d.before(lastMonth)) {
                _trace("- Deleting old event: [" + _getWhenDate(cee) + "] " + cee.getTitle().getPlainText());
                _gSrvc_delete(new URL(cee.getEditLink().getHref()));
            } else {
                _trace("--- This was not in the input data. It could have been deleted from Notes: " + _getWhenDate(cee) + " - " + cee.getTitle().getPlainText());
            }
        }

        _trace("Google_Calendar is now synchonized!");

    }

    private CalendarEventEntry createGoogleEventEntry(VEvent iCalEv, HashMap<String, CalendarEventEntry> curNotesEv) {

        CalendarEventEntry gEv;

        // Gets to update or create new to insert
        String NOTES_ID = _getStrValue(iCalEv, Property.UID);
        gEv = curNotesEv.remove(NOTES_ID);
        if (gEv == null) {
            gEv = new CalendarEventEntry();
        }

        // Sets basic fields + special NotesID field
        gEv.setTitle(new PlainTextConstruct(_getStrValue(iCalEv, Property.SUMMARY)));
        gEv.setContent(new PlainTextConstruct(_getStrValue(iCalEv, Property.DESCRIPTION)));
        ExtendedProperty ep = new ExtendedProperty();
        ep.setName(EPROP_NOTESID);
        ep.setValue(NOTES_ID);
        gEv.addExtendedProperty(ep);

        // Sets when it was published
        gEv.setPublished(new DateTime(iCalEv.getDateStamp().getDate(), MY_TZ));

        // Sets the initial and ending period of time + reminder
        DateTime startTime = new DateTime(iCalEv.getStartDate().getDate(), MY_TZ);
        DateTime endTime = new DateTime(iCalEv.getEndDate().getDate(), MY_TZ);
        When eventTimes = new When();
        eventTimes.setStartTime(startTime);
        eventTimes.setEndTime(endTime);
        Reminder rem = new Reminder();
        rem.setMethod(Method.ALERT);
        rem.setMinutes(5);
        eventTimes.setExtension(rem);
        gEv.getTimes().clear(); // Clear previous values before setting the new one
        gEv.addTime(eventTimes);

        // Sets where will be held the meeting
        Where location = new Where("", "", _getStrValue(iCalEv, Property.LOCATION));
        gEv.getLocations().clear(); // Clear previous values before setting the new one
        gEv.addLocation(location);

        // If it's a repeating event this has to be done
        Property rdate = iCalEv.getProperty(Property.RDATE);
        if (rdate != null) {
            String recurData;

            recurData = "";
            recurData += "DTSTART:" + _getStrValue(iCalEv, Property.DTSTART) + "\r\n";
            recurData += "DTEND:" + _getStrValue(iCalEv, Property.DTEND) + "\r\n";
            recurData += _calcRRule(_getStrValue(iCalEv, Property.RDATE)) + "\r\n";

            Recurrence recur = new Recurrence();
            recur.setValue(recurData);
            gEv.setRecurrence(recur);

            Reminder rem2 = new Reminder();
            rem2.setMethod(Method.ALERT);
            rem2.setMinutes(5);
            gEv.getRepeatingExtension(Reminder.class).clear();
            gEv.getRepeatingExtension(Reminder.class).add(rem2);
        }

        // Returns the new event
        return gEv;
    }

    private HashMap<String, CalendarEventEntry> getCurrentNotesEvents() throws Exception {

        HashMap<String, CalendarEventEntry> curEntries = new HashMap<String, CalendarEventEntry>();

        URL url = m_eventFeedUrl;
        while (url != null) {

            CalendarEventFeed cef = _gSrvc_getFeed(url, CalendarEventFeed.class);

            for (CalendarEventEntry cee : cef.getEntries()) {
                String notesID = _getNotesID(cee);
                if (notesID != null) {
                    curEntries.put(notesID, cee);
                }
            }

            if (cef.getNextLink() == null)
                url = null;
            else
                url = new URL(cef.getNextLink().getHref());
        }

        return curEntries;
    }

    private void initGoogle(Properties props) throws Exception {

        String userName = _getClearText(props.getProperty("user"));
        String pwd = _getClearText(props.getProperty("pwd"));

        System.setProperty("http.proxyHost", props.getProperty("proxy.host", ""));
        System.setProperty("http.proxyPort", props.getProperty("proxy.port", ""));
        System.setProperty("https.proxyHost", props.getProperty("proxy.host", ""));
        System.setProperty("https.proxyPort", props.getProperty("proxy.port", ""));

        m_myService = new CalendarService("JZB_GCal_Sync");
        m_myService.setUserCredentials(userName, pwd);

        // Mark the feed as an Event feed:
        new EventFeed().declareExtensions(m_myService.getExtensionProfile());
        new CalendarEventFeed().declareExtensions(m_myService.getExtensionProfile());

        m_eventFeedUrl = new URL("http://www.google.com/calendar/feeds/" + userName + "/private/full");

    }

    private VCalendar readICalFile(Properties props) throws Exception {

        File inFile;
        String fName;

        fName = props.getProperty("icalFile1", "");
        inFile = new File(fName);
        if (!inFile.exists()) {
            fName = props.getProperty("icalFile2", "");
            inFile = new File(fName);
        }
        if (!inFile.exists()) {
            throw new Exception("Both input files don't exists. Please check the names in the properties file.");
        }

        _trace("iCal info read from: " + fName);
        CalendarBuilder cb = new CalendarBuilder();
        FileInputStream fis = new FileInputStream(inFile);
        InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-15");

        VCalendar ical = cb.build(isr);

        return ical;
    }

}
