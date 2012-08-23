/**
 * 
 */
package com.jzb.nsf.gcal;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.EventFeed;
import com.google.gdata.data.extensions.Recurrence;
import com.google.gdata.data.extensions.Reminder;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
import com.google.gdata.data.extensions.Reminder.Method;
import com.jzb.nsf.notes.NotesCalData;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class GCalHelper {

    private static final String   LOCATION_NOTES_MARKER = "@N- ";
    private static final TimeZone MY_TZ                 = TimeZone.getDefault();

    private static final int      NUM_RETRIES           = 5;
    private URL                   m_eventFeedUrl;
    private boolean               m_justCheck;

    private CalendarService       m_myService;

    public GCalHelper(boolean justCheck) {
        m_justCheck = justCheck;
    }

    public static void encodeUserPwd(AppPreferences prefs, String newUser, String newPwd) {
        prefs.setPref("gcal.user", _getEncodedText(newUser));
        prefs.setPref("gcal.pwd", _getEncodedText(newPwd));
    }

    public void init(final AppPreferences prefs) throws Exception {

        Tracer._info("Initializing GCal conexion");

        String userName = _getClearText(prefs.getPref("gcal.user", ""));
        String pwd = _getClearText(prefs.getPref("gcal.pwd", ""));

        Tracer._debug("Checking 'use.proxy' preference");
        if (prefs.getPrefBool("use.proxy", true)) {
            Tracer._debug("Using proxy in order to connect to Google Calendar");
            System.setProperty("http.proxyHost", prefs.getPref("proxy.host", "cache.sscc.banesto.es"));
            System.setProperty("http.proxyPort", prefs.getPref("proxy.port", "8080"));
            System.setProperty("https.proxyHost", prefs.getPref("proxy.host", "cache.sscc.banesto.es"));
            System.setProperty("https.proxyPort", prefs.getPref("proxy.port", "8080"));
        }

        Tracer._debug("Connecting with Google Calendar Service");

        m_myService = new CalendarService("JZB_GCal_Sync");
        m_myService.setUserCredentials(userName, pwd);

        // Mark the feed as an Event feed:
        new EventFeed().declareExtensions(m_myService.getExtensionProfile());
        new CalendarEventFeed().declareExtensions(m_myService.getExtensionProfile());

        m_eventFeedUrl = new URL("http://www.google.com/calendar/feeds/" + userName + "/private/full");
    }

    public void syncCalendar(ArrayList<NotesCalData> ncdList) throws Exception {

        _deleteAllNotesEvents();
        _addNewNotesEntries(ncdList);

    }

    private void _addNewNotesEntries(ArrayList<NotesCalData> ncdList) throws Exception {

        Tracer._info("Adding new Notes entries to Google Calendar");
        for (NotesCalData ncd : ncdList) {
            CalendarEventEntry gEntry = _createGoogleEventEntry(ncd);
            if (gEntry != null) {
                Tracer._debug("* Inserting new event: [" + _getWhenDate(gEntry) + "] " + gEntry.getTitle().getPlainText());
                _gSrvc_insert(m_eventFeedUrl, gEntry);
            }
        }

    }

    private CalendarEventEntry _createGoogleEventEntry(NotesCalData ncd) throws Exception {

        Tracer._debug("Creating new GoogleEventEntry from Notes database element");

        CalendarEventEntry gEv = new CalendarEventEntry();

        // Sets basic fields
        gEv.setTitle(new PlainTextConstruct(ncd.Subject));
        String newNotesBody = "Chair: " + ncd.Chair + "\n\n" + ncd.Body;
        gEv.setContent(new PlainTextConstruct(newNotesBody));

        // Sets when it was published
        gEv.setPublished(new DateTime(ncd.getFirstStartDateTime(), MY_TZ));

        // Sets the initial and ending period of time + reminder
        DateTime startTime = new DateTime(ncd.getFirstStartDateTime(), MY_TZ);
        DateTime endTime = new DateTime(ncd.getFirstEndDateTime(), MY_TZ);

        // Sets where will be held the meeting (USES LOCATION FIELD TO MARK IT AS NOTES_ENTRY)
        Where location = new Where("", "", LOCATION_NOTES_MARKER + ncd.Location);
        gEv.getLocations().clear(); // Clear previous values before setting the new one
        gEv.addLocation(location);

        if (!ncd.Repeats) {

            // If it's a non-repeating event this has to be done
            When eventTimes = new When();
            eventTimes.setStartTime(startTime);
            eventTimes.setEndTime(endTime);
            Reminder rem = new Reminder();
            rem.setMethod(Method.ALERT);
            rem.setMinutes(5);
            eventTimes.setExtension(rem);
            gEv.getTimes().clear(); // Clear previous values before setting the new one
            gEv.addTime(eventTimes);

        } else {

            Tracer._warn("JUST weekly repeated appointments are understood. Others will be misunderstood");
            
            // If it's a repeating event this has to be done
            Recurrence recur = new Recurrence();
            recur.setValue(ncd.getRRule());
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

    private void _deleteAllNotesEvents() throws Exception {

        Tracer._info("* Deleting all previous Lotus Notes entries from Google Calendar");
        for (;;) {

            CalendarEventFeed cef = _gSrvc_getFeed(m_eventFeedUrl, CalendarEventFeed.class);

            for (CalendarEventEntry cee : cef.getEntries()) {
                if (isNotesEntry(cee))
                    _gSrvc_delete(new URL(cee.getEditLink().getHref()));
            }

            if (cef.getNextLink() == null)
                break;
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

    private static String _getEncodedText(String cad) {

        String msg = "";
        int n = 0;
        while (n < cad.length()) {
            char c = cad.charAt(n++);
            int i = ((c) & 0x000000FF) ^ 0x00000055;
            msg += Integer.toHexString(i);
        }
        return msg;

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

        if (m_justCheck)
            return;

        Exception ex = null;
        for (int n = 0; n < NUM_RETRIES; n++) {
            try {
                m_myService.delete(entryUrl);
                return;
            } catch (Exception e) {
                ex = e;
                Tracer._warn("Retrying to delete GCal entry. Error caught: " + ex.getClass().getName() + " - " + ex.getMessage());
            }
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
            Tracer._debug("Retrying. Error caught: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
        throw ex;
    }

    private <E extends BaseEntry<?>> E _gSrvc_insert(URL feedUrl, E entry) throws Exception {

        if (m_justCheck)
            return entry;

        Exception ex = null;
        for (int n = 0; n < NUM_RETRIES; n++) {
            try {
                return m_myService.insert(feedUrl, entry);
            } catch (Exception e) {
                ex = e;
            }
            Tracer._debug("Retrying. Error caught: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
        throw ex;
    }

    private boolean isNotesEntry(CalendarEventEntry cee) {

        for (Where w : cee.getLocations()) {
            String s = w.getValueString();
            if (s != null && s.startsWith(LOCATION_NOTES_MARKER))
                return true;
        }
        return false;

    }

}
