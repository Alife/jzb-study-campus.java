package com.jzb.tpoi.data;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 */

/**
 * @author n63636
 * 
 */
public class NMCollection<T_Item extends IIdentifiable> implements Iterable<T_Item> {

    // ---------------------------------------------------------------------------------
    @Target(FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface LinkedColl {

        public String name();
    }

    private HashMap<String, T_Item> m_items = new HashMap<String, T_Item>();
    private Method                  m_linkedGetter;
    private IIdentifiable           m_owner;

    // ---------------------------------------------------------------------------------
    public NMCollection(IIdentifiable owner) {
        m_owner = owner;
    }

    // ---------------------------------------------------------------------------------
    public boolean add(T_Item item) {

        T_Item oldItem = m_items.put(item.getId(), item);
        if (oldItem == null) {
            dlbLink(item);
            return true;
        }
        return false;
    }

    // ---------------------------------------------------------------------------------
    public boolean addAll(Collection<? extends T_Item> c) {
        boolean changed = false;
        for (T_Item item : c) {
            changed |= add(item);
        }
        return changed;
    }

    // ---------------------------------------------------------------------------------
    public void clear() {

        removeAll(new ArrayList(m_items.values()));
    }

    // ---------------------------------------------------------------------------------
    public boolean contains(T_Item item) {
        return m_items.containsKey(item.getId());
    }

    // ---------------------------------------------------------------------------------
    public void fixItemID(String old_ID) {

        T_Item item = m_items.get(old_ID);
        if (item != null && !item.getId().equals(old_ID)) {
            m_items.remove(old_ID);
            m_items.put(item.getId(), item);
        }

    }

    // ---------------------------------------------------------------------------------
    public T_Item getById(String id) {
        return m_items.get(id);
    }

    // ---------------------------------------------------------------------------------
    public boolean isEmpty() {
        return m_items.isEmpty();
    }

    // ---------------------------------------------------------------------------------
    /**
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<T_Item> iterator() {
        return m_items.values().iterator();
    }

    // ---------------------------------------------------------------------------------
    public boolean remove(T_Item item) {

        T_Item oldItem = m_items.remove(item.getId());
        if (oldItem != null) {
            dlbUnlink(item);
            return true;
        }
        return false;
    }

    // ---------------------------------------------------------------------------------
    public boolean removeAll(Collection<? extends T_Item> c) {
        boolean changed = false;
        for (T_Item item : c) {
            changed |= remove(item);
        }
        return changed;
    }

    // ---------------------------------------------------------------------------------
    public int size() {
        return m_items.size();
    }

    // ---------------------------------------------------------------------------------
    public Collection<T_Item> values() {
        return m_items.values();
    }

    // ---------------------------------------------------------------------------------
    protected void dlbLink(T_Item item) {
        if (m_owner != null) {
            NMCollection<IIdentifiable> partnerColl = getPartnerColl(item);
            if (partnerColl != null) {
                partnerColl.add(m_owner);
            }
        }
    }

    // ---------------------------------------------------------------------------------
    protected void dlbUnlink(T_Item item) {
        if (m_owner != null) {
            NMCollection<IIdentifiable> partnerColl = getPartnerColl(item);
            if (partnerColl != null) {
                partnerColl.remove(m_owner);
            }
        }
    }

    // ---------------------------------------------------------------------------------
    protected NMCollection<IIdentifiable> getPartnerColl(T_Item item) {

        try {
            Method m = getLinkedGetter(item);
            if (m != null) {
                return (NMCollection<IIdentifiable>) m.invoke(item);
            } else {
                return null;
            }
        } catch (Throwable th) {
            return null;
        }
    }

    // ---------------------------------------------------------------------------------
    private Method getLinkedGetter(IIdentifiable item) {

        if (m_linkedGetter != null) {
            return m_linkedGetter;
        }

        try {
            Field fields[] = m_owner.getClass().getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
                Object o = f.get(m_owner);
                if (this == o) {
                    LinkedColl a1 = f.getAnnotation(LinkedColl.class);
                    if (a1 != null) {
                        String getter = "get" + a1.name().substring(0, 1).toUpperCase() + a1.name().substring(1);
                        m_linkedGetter = item.getClass().getMethod(getter);
                        return m_linkedGetter;
                    }
                }
            }
        } catch (Throwable th) {
        }

        return null;
    }

}
