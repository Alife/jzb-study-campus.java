/**
 * Rigel Services Model Infrastructure, Version 1.0
 *
 * Copyright (C) 2002 ISBAN.
 * All Rights Reserved.
 *
 **/

package com.jzb.readers;

import java.io.File;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.jzb.model.BOImplementation;


/**
 * @author PS00A501
 *
 */
public class SQL_Loader extends Base_CompL_Loader  {
    
    protected SQL_Loader() {
        super();
    }
    
    public SQL_Loader(File compFile) throws Exception {
        super(compFile);
    }
    
    protected String getNamePrefix() {
        return "SQL_";
    }

    protected String getSampleContent() {
        return "<sqlComponent";
    }
    
    public Impl_Loader createLoader(File compFile) throws Exception {
        return new SQL_Loader(compFile);
    }
    
    protected void parseMethod(BOImplementation this_Comp, Document doc, Node methodNode) throws Exception {
    }        
    
}
