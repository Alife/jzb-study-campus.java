/**
 * 
 */
package com.jzb.nsf;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.TreeMap;

import com.jzb.nsf.gcal.GCalHelper;
import com.jzb.nsf.notes.NotesCalData;
import com.jzb.nsf.notes.NotesCalReader;
import com.jzb.swt.util.BaseWorker;
import com.jzb.swt.util.IProgressMonitor;
import com.jzb.util.AppPreferences;
import com.jzb.util.Tracer;

/**
 * @author n000013
 * 
 */
public class NotesSyncWorker extends BaseWorker {

    private static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public NotesSyncWorker(boolean justChecking, IProgressMonitor monitor) {
        super(justChecking, monitor);
    }

    public void syncCalendars(final AppPreferences prefs) {

        @SuppressWarnings("synthetic-access")
        ICallable callable = new ICallable() {

            public Object call() throws Exception {
                Tracer._info("** Synchronizing Google calendar with Notes Calendar info");
                boolean infoChanged =_syncCalendars(prefs);
                Tracer._info("** Synchronizing process done.");
                
                return infoChanged;
            }
        };

        _makeCall(callable);
    }

    private String _calcElementsDigest(ArrayList<NotesCalData> ncdList) throws Exception {

        // Los ordena aqui porque es mas rapido que en NOTES
        TreeMap<String, NotesCalData> tm = new TreeMap<String, NotesCalData>();
        for (NotesCalData ncd : ncdList) {
            tm.put(ncd.getFirstStartDateTime()+ncd.Subject, ncd);
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        for (NotesCalData ncd : tm.values()) {
            md.update(ncd.toString().getBytes());
        }
        byte[] bytes = md.digest();

        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int low = (bytes[i] & 0x0f);
            int high = ((bytes[i] & 0xf0) >> 4);
            sb.append(HEXADECIMAL[high]);
            sb.append(HEXADECIMAL[low]);
        }
        return sb.toString();
    }

    private boolean _isLotusNotesRunning() throws Exception {
        return WindowsUtils.listRunningProcesses().contains("nlnotes.exe");
    }

    private boolean _syncCalendars(final AppPreferences prefs) throws Exception {

        boolean infoChanged = false;
        
        if (!_isLotusNotesRunning()) {
            Tracer._info("* Lotus Notes is not running. Skipping synchronization.");
        } else {
            Tracer._debug("");
            NotesCalReader ncr = new NotesCalReader();
            ncr.init(prefs.getPref("nsf_dataSource", "*NOT_DEFINED*"));
            ArrayList<NotesCalData> ncdList = ncr.getAppointments();

            String prvDigest = prefs.getPref("prevAppDigest","*nothing*");
            String digest = _calcElementsDigest(ncdList);
            if (prvDigest.equals(digest)) {
                Tracer._info("* Lotus Notes info has not changed since last check");
            } else {
                Tracer._debug("");
                Tracer._info("* Lotus Notes info has changed");
                Tracer._debug("   previous Digest: "+prvDigest);
                Tracer._debug("   current Digest:  "+digest);
                Tracer._debug("");
                GCalHelper gch = new GCalHelper(m_justChecking);
                gch.init(prefs);
                gch.syncCalendar(ncdList);

                // Lo hace aqui para que reintente si falla lo anterior al tener la firma cambiada
                prefs.setPref("prevAppDigest",digest);
                
                infoChanged = true;
            }
        }
        
        return infoChanged;
    }

}
