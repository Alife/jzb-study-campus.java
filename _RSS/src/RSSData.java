/**
 * 
 */


import java.util.ArrayList;
import java.util.Date;

/**
 * @author n63636
 * 
 */
public class RSSData {

    public class Item {

        private String m_description;

        private Date   m_pubDate;

        private String m_title;

        public Item(String title, String description, Date pubDate) {
            m_title = title;
            m_description = description;
            m_pubDate = pubDate;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return m_description;
        }

        /**
         * @return the pubDate
         */
        public Date getPubDate() {
            return m_pubDate;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return m_title;
        }

        /**
         * @param description
         *            the description to set
         */
        public void setDescription(String description) {
            m_description = description;
        }

        /**
         * @param pubDate
         *            the pubDate to set
         */
        public void setPubDate(Date pubDate) {
            m_pubDate = pubDate;
        }

        /**
         * @param title
         *            the title to set
         */
        public void setTitle(String title) {
            m_title = title;
        }

    }

    private String          m_description;
    private String          m_link;
    private String          m_title;
    private ArrayList<Item> m_items = new ArrayList<Item>();

    public RSSData(String title, String description, String link) {
        m_title = title;
        m_description = description;
        m_link = link;
    }

    public void addItem(String title, String description, Date pubDate) {
        m_items.add(new Item(title, description, pubDate));
    }

    public void addItem(Item item) {
        m_items.add(item);
    }

    public ArrayList<Item> getItems() {
        return m_items;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return m_link;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        m_description = description;
    }

    /**
     * @param link
     *            the link to set
     */
    public void setLink(String link) {
        m_link = link;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        m_title = title;
    }
}
