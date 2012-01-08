/**
 * 
 */
package com.jzb.tpoi.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author n63636
 * 
 */
public abstract class TBaseEntity implements Externalizable {

    // Otros interesantes: Authors, ETag
    // Coincidira con la URL de edicion

    private static final String LOCAL_ID_PREFIX = "@cafe0fea0";
    private String              m_description;
    // Solo por motivos de velocidad y evitar recalculos
    private String              m_displayName;
    private TIcon               m_icon;
    private String              m_id;
    private boolean             m_markedAsDeleted;
    private String              m_name;
    // Al mostrar un texto o al comparar, se utilizara este valor si no es null
    private String              m_shortName;
    private SyncStatusType      m_syncStatus;
    private TDateTime           m_ts_created;
    private TDateTime           m_ts_updated;

    private EntityType          m_type;

    // ---------------------------------------------------------------------------------
    public static boolean _isLocalId(String id) {
        return id.startsWith(LOCAL_ID_PREFIX);
    }

    // ---------------------------------------------------------------------------------
    private static String _calcLocalId() {
        return LOCAL_ID_PREFIX + System.currentTimeMillis();
    }

    // ---------------------------------------------------------------------------------
    protected TBaseEntity(EntityType type) {

        m_type = type;
        m_id = _calcLocalId();
        m_name = "";
        m_description = "";
        m_shortName = null;
        // @todo uno por defecto m_icon = ""
        m_ts_created = m_ts_updated = new TDateTime(System.currentTimeMillis());
        m_markedAsDeleted = false;
        m_syncStatus = SyncStatusType.Sync_OK;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the description
     */
    public String getDescription() {
        return m_description;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the name or shortname
     */
    public String getDisplayName() {
        return m_displayName;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the icon
     */
    public TIcon getIcon() {
        return m_icon;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the id
     */
    public String getId() {
        return m_id;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the name
     */
    public String getName() {
        return m_name;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the shortName
     */
    public String getShortName() {
        return m_shortName;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the syncStatus
     */
    public SyncStatusType getSyncStatus() {
        return m_syncStatus;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the ts_created
     */
    public TDateTime getTS_Created() {
        return m_ts_created;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the ts_updated
     */
    public TDateTime getTS_Updated() {
        return m_ts_updated;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the type
     */
    public EntityType getType() {
        return m_type;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the markedAsDeleted
     */
    public boolean isMarkedAsDeleted() {
        return m_markedAsDeleted;
    }

    // ---------------------------------------------------------------------------------
    // Un ID "local" no tendra la parte de URL. Un ID "remoto" si la tendra
    public boolean isSynchronized() {
        return m_id.contains("maps.google.com");
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param markedAsDeleted
     *            the markedAsDeleted to set
     */
    public void markedAsDeleted(boolean markedAsDeleted) {
        m_markedAsDeleted = markedAsDeleted;
        touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        m_type = (EntityType) in.readObject();
        m_id = (String) in.readObject();
        m_name = (String) in.readObject();
        m_shortName = (String) in.readObject();
        m_description = (String) in.readObject();
        String iconName = (String) in.readObject();
        if (iconName != null)
            m_icon = TIcon.createFromName(iconName);
        m_ts_created = new TDateTime(in.readLong());
        m_ts_updated = new TDateTime(in.readLong());
        m_markedAsDeleted = in.readBoolean();
        m_syncStatus = (SyncStatusType) in.readObject();

        _recalcDisplayName();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        m_description = description == null ? null : description.trim();
        touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param icon
     *            the icon to set
     */
    public void setIcon(TIcon icon) {
        m_icon = icon;
        touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {

        m_name = name == null ? null : name.trim();

        _recalcDisplayName();

        touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param shortName
     *            the shortName to set
     */
    public void setShortName(String shortName) {

        m_shortName = shortName == null ? null : shortName.trim();

        _recalcDisplayName();

        touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param syncStatus
     *            the syncStatus to set
     */
    public void setSyncStatus(SyncStatusType syncStatus) {
        m_syncStatus = syncStatus;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param ts_created
     *            the created_ts to set
     */
    public void setTS_Created(TDateTime ts_created) {
        m_ts_created = ts_created;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param ts_updated
     *            the ts_updated_ts to set
     */
    public void setTS_Updated(TDateTime ts_updated) {
        m_ts_updated = ts_updated;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toXmlString();
    }

    // ---------------------------------------------------------------------------------
    public void touchAsUpdated() {
        m_ts_updated = new TDateTime(System.currentTimeMillis());
    }

    // ---------------------------------------------------------------------------------
    public String toXmlString() {
        return toXmlString("");
    }

    // ---------------------------------------------------------------------------------
    public String toXmlString(String ident) {
        StringBuffer sb = new StringBuffer();
        xmlStringBTag(sb, ident);
        xmlStringBody(sb, ident + "  ");
        xmlStringETag(sb, ident);
        return sb.toString();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param id
     *            the id to set
     */
    public void updateId(String id) {
        m_id = id;
        touchAsUpdated();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {

        out.writeObject(m_type);
        out.writeObject(m_id);
        out.writeObject(m_name);
        out.writeObject(m_shortName);
        out.writeObject(m_description);
        if (m_icon != null)
            out.writeObject(m_icon.getName());
        else
            out.writeObject(null);
        out.writeLong(m_ts_created.getValue());
        out.writeLong(m_ts_updated.getValue());
        out.writeBoolean(m_markedAsDeleted);
        out.writeObject(m_syncStatus);
    }

    // ---------------------------------------------------------------------------------
    protected void xmlStringBody(StringBuffer sb, String ident) {
        sb.append(ident).append("<id>").append(m_id != null ? m_id : "").append("</id>\n");
        sb.append(ident).append("<name>").append(m_name != null ? m_name : "").append("</name>\n");
        sb.append(ident).append("<shortName>").append(m_shortName != null ? m_shortName : "").append("</shortName>\n");
        sb.append(ident).append("<description>").append(m_description != null ? m_description : "").append("</description>\n");
        sb.append(ident).append("<icon>").append(m_icon != null ? m_icon : "").append("</icon>\n");
        sb.append(ident).append("<ts_created>").append(m_ts_created != null ? m_ts_created : "").append("</ts_created>\n");
        sb.append(ident).append("<ts_updated>").append(m_ts_updated != null ? m_ts_updated : "").append("</ts_updated>\n");
        sb.append(ident).append("<syncStatus>").append(m_syncStatus != null ? m_syncStatus : "").append("</syncStatus>\n");
        sb.append(ident).append("<markedAsDeleted>").append(m_markedAsDeleted).append("</markedAsDeleted>\n");
    }

    // ---------------------------------------------------------------------------------
    protected void xmlStringBTag(StringBuffer sb, String ident) {
        sb.append(ident).append('<').append(m_type).append(">\n");
    }

    // ---------------------------------------------------------------------------------
    protected void xmlStringETag(StringBuffer sb, String ident) {
        sb.append(ident).append("</").append(m_type).append('>');
    }

    // ---------------------------------------------------------------------------------
    private void _recalcDisplayName() {
        if (m_shortName == null || m_shortName.length() == 0)
            m_displayName = m_name;
        else
            m_displayName = m_shortName;
    }
}
