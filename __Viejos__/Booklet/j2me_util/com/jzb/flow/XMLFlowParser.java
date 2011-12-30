/**
 * 
 */
package com.jzb.flow;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

import com.jzb.j2me.util.Prop;
import com.jzb.j2me.util.Properties;

/**
 * @author PS00A501
 * 
 */
class XMLFlowParser {

    private static Properties      s_defStates;
    private static final String    TAG_NAME_FLOW        = "flow";

    private static final String    TAG_NAME_INITIALIZER = "initializer";
    private static final String    TAG_NAME_DATA        = "data";
    private static final String    TAG_NAME_STATE       = "state";
    private static final String    TAG_NAME_ST_PARAM    = "param";
    private static final String    TAG_NAME_ST_TR       = "tr";
    private static final String    TAG_NAME_GLOBALTR    = "globalTr";
    private static final int       TAG_ID_FLOW          = 0x10;

    private static final int       TAG_ID_INITIALIZER   = 0x20;
    private static final int       TAG_ID_DATA          = 0x30;
    private static final int       TAG_ID_STATE         = 0x40;
    private static final int       TAG_ID_ST_PARAM      = 0x50;
    private static final int       TAG_ID_ST_TR         = 0x60;
    private static final int       TAG_ID_GLOBALTR      = 0x70;
    private static final Hashtable TAG_IDs              = new Hashtable();

    static {
        try {
            s_defStates = Prop.load(XMLFlowParser.class.getResourceAsStream("def_states.properties"));
        } catch (IOException ex) {
            s_defStates = new Properties();
        }
    }
    static {
        TAG_IDs.put(TAG_NAME_FLOW, new Integer(TAG_ID_FLOW));
        TAG_IDs.put(TAG_NAME_INITIALIZER, new Integer(TAG_ID_INITIALIZER));
        TAG_IDs.put(TAG_NAME_DATA, new Integer(TAG_ID_DATA));
        TAG_IDs.put(TAG_NAME_STATE, new Integer(TAG_ID_STATE));
        TAG_IDs.put(TAG_NAME_ST_PARAM, new Integer(TAG_ID_ST_PARAM));
        TAG_IDs.put(TAG_NAME_ST_TR, new Integer(TAG_ID_ST_TR));
        TAG_IDs.put(TAG_NAME_GLOBALTR, new Integer(TAG_ID_GLOBALTR));
    }

    public static void addStates(Properties props) {
        s_defStates.addAllProperties(props);
    }

    public static Flow parseFlow(XMLFlow flow, String flowName) throws Exception {

        KXmlParser parser = new KXmlParser();
        parser.setInput(new InputStreamReader(Class.class.getResourceAsStream("/flows/" + flowName + ".xml")));

        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, TAG_NAME_FLOW);

        Vector states = new Vector();
        Vector trans = new Vector();

        parser.nextTag();
        while (parser.getEventType() != XmlPullParser.END_TAG) {

            switch (getTagID(parser.getName())) {
                case TAG_ID_INITIALIZER:
                    processTag_Initializer(parser);
                    break;

                case TAG_ID_DATA:
                    processTag_Data(parser);
                    break;

                case TAG_ID_STATE:
                    processTag_State(flow, parser, states, trans);
                    break;

                case TAG_ID_GLOBALTR:
                    processTag_GlobalTr(parser, trans);
                    break;

                default:
                    throw new Exception("Unexpected tag name '" + parser.getName() + "' found");
            }

            parser.nextTag();
        }

        flow.initFlow(states, trans);

        return flow;
    }

    private static int getTagID(String tagName) {
        Integer i = (Integer) TAG_IDs.get(tagName);
        if (i == null)
            return -1;
        else
            return i.intValue();
    }

    private static IXMLInitializable loadState(String stType, String stClass) throws Exception {

        if (stType != null && stType.length() == 0) {
            stType = null;
        }

        if (stClass != null && stClass.length() == 0) {
            stClass = null;
        }

        if (stClass == null && stType != null && s_defStates != null) {
            stClass = s_defStates.getProperty(stType);
        }

        if (stClass == null) {
            throw new Exception("State class name not found for: type='" + stType + "' and class='" + stClass + "'");
        }

        Class clazz = Class.forName(stClass);
        IXMLInitializable state = (IXMLInitializable) clazz.newInstance();
        return state;
    }

    private static void processTag_Data(KXmlParser parser) throws Exception {
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, TAG_NAME_DATA);
    }

    private static void processTag_GlobalTr(KXmlParser parser, Vector trans) throws Exception {

        trans.addElement(new String[] { "*", parser.getAttributeValue("ev"), parser.getAttributeValue("st") });

        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, TAG_NAME_GLOBALTR);
    }

    private static void processTag_Initializer(KXmlParser parser) throws Exception {
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, null, TAG_NAME_INITIALIZER);
    }

    private static void processTag_State(Flow owner, KXmlParser parser, Vector states, Vector trans) throws Exception {

        String stName = parser.getAttributeValue("id");
        String stType = parser.getAttributeValue("type");
        String stClass = parser.getAttributeValue("class");

        Properties params = new Properties();
        for (int n = 0; n < parser.getAttributeCount(); n++) {
            String name = parser.getAttributeName(n);
            if (name.equals("type") || name.equals("class"))
                continue;
            params.addProperty(name, parser.getAttributeValue(n));
        }

        boolean done = false;
        while (!done) {

            parser.nextTag();

            switch (getTagID(parser.getName())) {
                case TAG_ID_ST_PARAM:
                    params.addProperty(parser.getAttributeValue("name"), parser.getAttributeValue("value"));
                    parser.nextTag();
                    break;

                case TAG_ID_ST_TR:
                    trans.addElement(new String[] { stName, parser.getAttributeValue("ev"), parser.getAttributeValue("st") });
                    parser.nextTag();
                    break;

                default:
                    done = true;
                    break;
            }
        }

        try {
            IXMLInitializable state = loadState(stType, stClass);
            state.init(owner, stName, params);
            states.addElement(state);
        } catch (Exception ex) {
            throw new Exception("Error creating state '" + stName + "': " + ex.getMessage());
        }

    }

}
