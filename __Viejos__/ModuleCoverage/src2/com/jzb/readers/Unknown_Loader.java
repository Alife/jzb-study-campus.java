/**
 * Rigel Services Model Infrastructure, Version 1.0 Copyright (C) 2002 ISBAN. All Rights Reserved.
 */

package com.jzb.readers;

import java.io.File;
import java.util.ArrayList;


import org.w3c.dom.Document;


/**
 * @author PS00A501
 */
public class Unknown_Loader extends Impl_Loader {

    protected Unknown_Loader() {
        super();
    }

    public Unknown_Loader(File compFile) throws Exception {
        super(compFile);
    }

    public boolean acceptByContent(String sample) {
        return sample.indexOf("<contextBean")>=0 
                || sample.indexOf("<businessException")>=0;
    }

    public Impl_Loader createLoader(File compFile) throws Exception {
        return new Unknown_Loader(compFile);
    }

    protected void parseDOM(Document doc, ArrayList list) throws Exception {

    }
    
}
