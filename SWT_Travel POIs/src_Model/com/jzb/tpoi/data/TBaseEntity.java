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
public abstract class TBaseEntity implements IIdentifiable, ITouchable, Externalizable {

    // Otros interesantes: Authors, ETag
    // Coincidira con la URL de edicion

    private static final String LOCAL_ETAG_PREFIX  = "@Local-";
    private static final String LOCAL_ID_PREFIX    = "@cafe-";
    private static final String REMOTE_ETAG_PREFIX = "@Sync-";

    private static long         s_idCounter        = Math.round(100L * Math.random());

    private boolean             m_changed;
    private String              m_description;
    private TIcon               m_icon;
    private String              m_id;
    private boolean             m_markedAsDeleted;
    private String              m_name;
    private String              m_shortName;
    private String              m_syncETag;
    private TDateTime           m_ts_created;
    private TDateTime           m_ts_updated;
    private EntityType          m_type;

    // Solo por motivos de velocidad y evitar recalculos
    private String              t_displayName;
    private SyncStatusType      t_syncStatus;

    // ---------------------------------------------------------------------------------
    public static synchronized String _calcRemoteCategoryETag() {
        s_idCounter++;
        return REMOTE_ETAG_PREFIX + System.currentTimeMillis() + "-" + s_idCounter;
    }

    // ---------------------------------------------------------------------------------
    private static synchronized String _calcLocalETag() {
        s_idCounter++;
        return LOCAL_ETAG_PREFIX + System.currentTimeMillis() + "-" + s_idCounter;
    }

    // ---------------------------------------------------------------------------------
    private static synchronized String _calcLocalId() {
        s_idCounter++;
        return LOCAL_ID_PREFIX + System.currentTimeMillis() + "-" + s_idCounter;
    }

    // ---------------------------------------------------------------------------------
    // Just to be called from chidren
    TBaseEntity() {

    }

    // ---------------------------------------------------------------------------------
    protected TBaseEntity(EntityType type) {

        m_type = type;
        m_id = _calcLocalId();
        m_name = "";
        m_shortName = null;
        m_description = "";
        m_icon = getDefaultIcon();
        m_changed = false;
        m_markedAsDeleted = false;
        m_syncETag = _calcLocalETag();
        t_syncStatus = SyncStatusType.Sync_OK;

        m_ts_created = m_ts_updated = TDateTime.now();

    }

    // ---------------------------------------------------------------------------------
    public void mergeFrom(TBaseEntity other, boolean conflict) {

        m_type = other.m_type;
        m_id = other.m_id;
        m_name = other.m_name;
        m_shortName = other.m_shortName;
        m_syncETag = other.m_syncETag;
        m_description = other.m_description;
        m_icon = other.m_icon;
        m_markedAsDeleted = other.m_markedAsDeleted;
        m_ts_created = other.m_ts_created;
        m_ts_updated = other.m_ts_updated;
    }

    // ---------------------------------------------------------------------------------
    public void clearUpdated() {
        m_changed = false;
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
        return t_displayName;
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
     * @return the syncETag
     */
    public String getSyncETag() {
        return m_syncETag;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the syncStatus
     */
    public SyncStatusType getSyncStatus() {
        return t_syncStatus;
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
    public boolean infoEquals(TBaseEntity obj) {

        if (obj == null)
            return false;

        if (m_markedAsDeleted != obj.m_markedAsDeleted)
            return false;
        if (!m_id.equals(obj.m_id))
            return false;
        if (!m_name.endsWith(obj.m_name))
            return false;
        if (!m_syncETag.endsWith(obj.m_syncETag))
            return false;
        if (!m_description.endsWith(obj.m_description))
            return false;
        if (m_shortName==null && obj.m_shortName!=null)
            return false;
        if(m_shortName!=null && !m_shortName.equals(obj.m_shortName))
            return false;
        if (!m_icon.equals(obj.m_icon))
            return false;
        if (!m_ts_created.equals(obj.m_ts_created))
            return false;
//        if (!m_ts_updated.equals(obj.m_ts_updated))
//            return false;

        return true;

    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the changed
     */
    public boolean isChanged() {
        return m_changed;
    }

    // ---------------------------------------------------------------------------------
    public boolean isLocal() {
        return m_syncETag.startsWith(LOCAL_ETAG_PREFIX);
    }

    // ---------------------------------------------------------------------------------
    /**
     * @return the markedAsDeleted
     */
    public boolean isMarkedAsDeleted() {
        return m_markedAsDeleted;
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param markedAsDeleted
     *            the markedAsDeleted to set
     */
    public void markedAsDeleted(boolean markedAsDeleted) {
        m_markedAsDeleted = markedAsDeleted;

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
        m_icon = TIcon.createFromSmallURL((String) in.readObject());
        m_changed = in.readBoolean();
        m_syncETag = (String) in.readObject();
        m_markedAsDeleted = in.readBoolean();
        m_ts_created = new TDateTime(in.readLong());
        m_ts_updated = new TDateTime(in.readLong());

        _recalcDisplayName();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        m_description = description == null ? null : description.trim();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param icon
     *            the icon to set
     */
    public void setIcon(TIcon icon) {
        if (icon != null)
            m_icon = icon;
        else
            m_icon = getDefaultIcon();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {

        m_name = name == null ? "" : name.trim();

        _recalcDisplayName();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param shortName
     *            the shortName to set
     */
    public void setShortName(String shortName) {

        m_shortName = shortName == null ? null : shortName.trim();

        _recalcDisplayName();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param syncStatus
     *            the syncStatus to set
     */
    public void setSyncStatus(SyncStatusType syncStatus) {
        t_syncStatus = syncStatus;
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
        m_changed = true;
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
    }

    // ---------------------------------------------------------------------------------
    /**
     * @param syncETag
     *            the syncETag to set
     */
    public void updateSyncETag(String syncETag) {
        m_syncETag = syncETag;
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
        out.writeObject(m_icon.getSmallUrl());
        out.writeBoolean(m_changed);
        out.writeObject(m_syncETag);
        out.writeBoolean(m_markedAsDeleted);
        out.writeLong(m_ts_created.getValue());
        out.writeLong(m_ts_updated.getValue());
    }

    // ---------------------------------------------------------------------------------
    protected abstract TIcon getDefaultIcon();

    // ---------------------------------------------------------------------------------
    protected void xmlStringBody(StringBuffer sb, String ident) {

        sb.append(ident).append("<id>").append(m_id).append("</id>\n");
        sb.append(ident).append("<name>").append(m_name).append("</name>\n");
        sb.append(ident).append("<shortName>").append(m_shortName != null ? m_shortName : "").append("</shortName>\n");
        sb.append(ident).append("<syncETag>").append(m_syncETag).append("</syncETag>\n");
        sb.append(ident).append("<syncStatus>").append(t_syncStatus).append("</syncStatus>\n");
        sb.append(ident).append("<changed>").append(m_changed).append("</changed>\n");
        sb.append(ident).append("<description>").append(m_description).append("</description>\n");
        sb.append(ident).append("<icon>").append(m_icon).append("</icon>\n");
        sb.append(ident).append("<markedAsDeleted>").append(m_markedAsDeleted).append("</markedAsDeleted>\n");
        sb.append(ident).append("<ts_created>").append(m_ts_created).append("</ts_created>\n");
        sb.append(ident).append("<ts_updated>").append(m_ts_updated).append("</ts_updated>\n");
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
            t_displayName = m_name;
        else
            t_displayName = m_shortName;
    }
}
