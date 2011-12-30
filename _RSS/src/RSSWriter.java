/**
 * 
 */


import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author n63636
 * 
 */
public class RSSWriter {

    private SimpleDateFormat m_sdf = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z", Locale.US);
    private RSSData          m_rss;
    PrintStream              m_ps;

    public RSSWriter(RSSData rss, PrintStream ps) {
        m_rss=rss;
        m_ps=ps;
    }
    
    public void write() throws Exception {
        writeRSS_Header();
        for (RSSData.Item item : m_rss.getItems()) {
            writeRSS_Item(item);
        }
        writeRSS_Footer();
    }

    private void writeRSS_Header() throws Exception {

        m_ps.println("<rss version=\"2.0\">");
        m_ps.println("  <channel>");
        m_ps.println("    <title>" + m_rss.getTitle() + "</title>");
        m_ps.println("    <link>" + m_rss.getLink() + "</link>");
        m_ps.println("    <description>" + m_rss.getDescription() + "</description>");
    }

    private void writeRSS_Item(RSSData.Item item) throws Exception {

        m_ps.println("    <item>");
        m_ps.println("      <title>" + item.getTitle() + "</title>");
        m_ps.println("      <description>" + item.getDescription() + "</description>");
        m_ps.println("      <pubDate>" + m_sdf.format(item.getPubDate()) + "</pubDate>");
        m_ps.println("    </item>");

    }

    private void writeRSS_Footer() throws Exception {
        m_ps.println("  </channel>");
        m_ps.println("</rss>");
    }
}
