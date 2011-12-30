/**
 * 
 */
package com.jzb.nsf.notes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import com.jzb.swt.util.ITracer;

/**
 * @author n000013
 * 
 */
public class NotesTester {

    ITracer m_trz;

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
            NotesTester me = new NotesTester();
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

        processRTimes(null);

        // m_trz = new PStreamTracer(System.out);
        // NotesCalReader ncReader = new NotesCalReader(m_trz);
        //
        // ncReader.init("NSF_JZarzuela"); // NSF_Local
        // ArrayList<NotesCalData> ncList = ncReader.getAppointments();
        // for (NotesCalData ncd : ncList) {
        // if (ncd.Subject.equals("INGLES - R4")) {
        // processRTimes(ncd);
        // }
        // }

    }

    private void processRTimes(NotesCalData ncd) throws Exception {
        // System.out.println(ncd.getRRule());

        ArrayList<Calendar> cals = _getRepeatingCalendar(s_dates);
        _checkDaily(cals);
    }

    private boolean _checkDaily(ArrayList<Calendar> cals) {

        long t1 = cals.get(1).getTimeInMillis() - cals.get(0).getTimeInMillis();

        for (int n = 1; n < cals.size(); n++) {
            long t2 = cals.get(n).getTimeInMillis() - cals.get(n - 1).getTimeInMillis();
            if (t1 != t2)
                return false;
        }

        return true;
    }

    private static SimpleDateFormat s_sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

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

    private String s_dates = "22/09/2009 13:30:00;24/09/2009 13:30:00;29/09/2009 13:30:00;01/10/2009 13:30:00;06/10/2009 13:30:00;08/10/2009 13:30:00;13/10/2009 13:30:00;15/10/2009 13:30:00;20/10/2009 13:30:00;22/10/2009 13:30:00;27/10/2009 13:30:00;29/10/2009 13:30:00;03/11/2009 13:30:00;05/11/2009 13:30:00;10/11/2009 13:30:00;12/11/2009 13:30:00;17/11/2009 13:30:00;19/11/2009 13:30:00;24/11/2009 13:30:00;26/11/2009 13:30:00;01/12/2009 13:30:00;03/12/2009 13:30:00;08/12/2009 13:30:00;10/12/2009 13:30:00;15/12/2009 13:30:00;17/12/2009 13:30:00;22/12/2009 13:30:00;24/12/2009 13:30:00;29/12/2009 13:30:00;31/12/2009 13:30:00;05/01/2010 13:30:00;07/01/2010 13:30:00;12/01/2010 13:30:00;14/01/2010 13:30:00;19/01/2010 13:30:00;21/01/2010 13:30:00;26/01/2010 13:30:00;28/01/2010 13:30:00;02/02/2010 13:30:00;04/02/2010 13:30:00;09/02/2010 13:30:00;11/02/2010 13:30:00;16/02/2010 13:30:00;18/02/2010 13:30:00;23/02/2010 13:30:00;25/02/2010 13:30:00;02/03/2010 13:30:00;04/03/2010 13:30:00;09/03/2010 13:30:00;11/03/2010 13:30:00;16/03/2010 13:30:00;18/03/2010 13:30:00;23/03/2010 13:30:00;25/03/2010 13:30:00;30/03/2010 13:30:00;01/04/2010 13:30:00;06/04/2010 13:30:00;08/04/2010 13:30:00;13/04/2010 13:30:00;15/04/2010 13:30:00;20/04/2010 13:30:00;22/04/2010 13:30:00;27/04/2010 13:30:00;29/04/2010 13:30:00;04/05/2010 13:30:00;06/05/2010 13:30:00;11/05/2010 13:30:00;13/05/2010 13:30:00;18/05/2010 13:30:00;20/05/2010 13:30:00;25/05/2010 13:30:00;27/05/2010 13:30:00;01/06/2010 13:30:00;03/06/2010 13:30:00;08/06/2010 13:30:00;10/06/2010 13:30:00;15/06/2010 13:30:00;17/06/2010 13:30:00;22/06/2010 13:30:00;24/06/2010 13:30:00;29/06/2010 13:30:00;01/07/2010 13:30:00;06/07/2010 13:30:00;08/07/2010 13:30:00;13/07/2010 13:30:00;15/07/2010 13:30:00;20/07/2010 13:30:00;22/07/2010 13:30:00;27/07/2010 13:30:00;29/07/2010 13:30:00;03/08/2010 13:30:00;05/08/2010 13:30:00;10/08/2010 13:30:00;12/08/2010 13:30:00;17/08/2010 13:30:00;19/08/2010 13:30:00;24/08/2010 13:30:00;26/08/2010 13:30:00;31/08/2010 13:30:00;02/09/2010 13:30:00;07/09/2010 13:30:00;09/09/2010 13:30:00;14/09/2010 13:30:00;16/09/2010 13:30:00;21/09/2010 13:30:00";


}
