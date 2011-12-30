package com.meterware.httpunit.parsing;
import com.meterware.httpunit.scripting.ScriptingHandler;

import org.w3c.dom.html.HTMLDocument;

import java.io.IOException;


/**
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
public interface DocumentAdapter {


    /**
     * Records the root (Document) node.
     */
    public void setDocument( HTMLDocument document );


    /**
     * Returns the contents of an included script, given its src attribute.
     * @param srcAttribute the relative URL for the included script
     * @return the contents of the script.
     * @throws java.io.IOException if there is a problem retrieving the script
     */
    public String getIncludedScript( String srcAttribute ) throws IOException;


    /**
     * Returns the Scriptable object associated with the document
     */
    public ScriptingHandler getScriptingHandler();
}
