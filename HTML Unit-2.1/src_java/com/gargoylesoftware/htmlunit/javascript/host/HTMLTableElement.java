/*
 * Copyright (c) 2002-2008 Gargoyle Software Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include the following acknowledgment:
 *
 *       "This product includes software developed by Gargoyle Software Inc.
 *        (http://www.GargoyleSoftware.com/)."
 *
 *    Alternately, this acknowledgment may appear in the software itself, if
 *    and wherever such third-party acknowledgments normally appear.
 * 4. The name "Gargoyle Software" must not be used to endorse or promote
 *    products derived from this software without prior written permission.
 *    For written permission, please contact info@GargoyleSoftware.com.
 * 5. Products derived from this software may not be called "HtmlUnit", nor may
 *    "HtmlUnit" appear in their name, without prior written permission of
 *    Gargoyle Software Inc.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL GARGOYLE
 * SOFTWARE INC. OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gargoylesoftware.htmlunit.javascript.host;

import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * A JavaScript object representing a Table.
 *
 * @version $Revision: 2905 $
 * @author David D. Kilzer
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Daniel Gredler
 * @author Chris Erskine
 * @author Marc Guillemot
 * @author Ahmed Ashour
 */
public class HTMLTableElement extends RowContainer {

    private static final long serialVersionUID = 2779888994049521608L;
    private HTMLCollection tBodies_; // has to be a member to have equality (==) working

    /**
     * Create an instance.
     */
    public HTMLTableElement() {
    }

    /**
     * JavaScript constructor. This must be declared in every JavaScript file because
     * the Rhino engine won't walk up the hierarchy looking for constructors.
     */
    @Override
    public void jsConstructor() {
    }

    /**
     * Returns the table's caption element, or <tt>null</tt> if none exists. If more than one
     * caption is declared in the table, this method returns the first one.
     * @return the table's caption element
     */
    public Object jsxGet_caption() {
        final List< ? extends HtmlElement> captions = getHtmlElementOrDie().getHtmlElementsByTagName("caption");
        if (captions.isEmpty()) {
            return null;
        }
        return getScriptableFor(captions.get(0));
    }

    /**
     * Returns the table's tfoot element, or <tt>null</tt> if none exists. If more than one
     * tfoot is declared in the table, this method returns the first one.
     * @return the table's tfoot element
     */
    public Object jsxGet_tFoot() {
        final List< ? extends HtmlElement> tfoots = getHtmlElementOrDie().getHtmlElementsByTagName("tfoot");
        if (tfoots.isEmpty()) {
            return null;
        }
        return getScriptableFor(tfoots.get(0));
    }

    /**
     * Returns the table's thead element, or <tt>null</tt> if none exists. If more than one
     * thead is declared in the table, this method returns the first one.
     * @return the table's thead element
     */
    public Object jsxGet_tHead() {
        final List< ? extends HtmlElement> theads = getHtmlElementOrDie().getHtmlElementsByTagName("thead");
        if (theads.isEmpty()) {
            return null;
        }
        return getScriptableFor(theads.get(0));
    }

    /**
     * Returns the tbody's in the table.
     * @return the tbody's in the table
     */
    public Object jsxGet_tBodies() {
        if (tBodies_ == null) {
            tBodies_ = new HTMLCollection(this);
            tBodies_.init(getDomNodeOrDie(), "./tbody");
        }
        return tBodies_;
    }

    /**
     * If this table does not have a caption, this method creates an empty table caption,
     * adds it to the table and then returns it. If one or more captions already exist,
     * this method returns the first existing caption.
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/methods/createcaption.asp">
     * MSDN Documentation</a>
     * @return a newly added caption if no caption exists, or the first existing caption
     */
    public Object jsxFunction_createCaption() {
        return getScriptableFor(getHtmlElementOrDie().appendChildIfNoneExists("caption"));
    }

    /**
     * If this table does not have a tfoot element, this method creates an empty tfoot
     * element, adds it to the table and then returns it. If this table already has a
     * tfoot element, this method returns the existing tfoot element.
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/methods/createtfoot.asp">
     * MSDN Documentation</a>
     * @return a newly added caption if no caption exists, or the first existing caption
     */
    public Object jsxFunction_createTFoot() {
        return getScriptableFor(getHtmlElementOrDie().appendChildIfNoneExists("tfoot"));
    }

    /**
     * If this table does not have a thead element, this method creates an empty
     * thead element, adds it to the table and then returns it. If this table
     * already has a thead element, this method returns the existing thead element.
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/methods/createthead.asp">
     * MSDN Documentation</a>
     * @return a newly added caption if no caption exists, or the first existing caption
     */
    public Object jsxFunction_createTHead() {
        return getScriptableFor(getHtmlElementOrDie().appendChildIfNoneExists("thead"));
    }

    /**
     * Deletes this table's caption. If the table has multiple captions, this method
     * deletes only the first caption. If this table does not have any captions, this
     * method does nothing.
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/methods/deletecaption.asp">
     * MSDN Documentation</a>
     */
    public void jsxFunction_deleteCaption() {
        getHtmlElementOrDie().removeChild("caption", 0);
    }

    /**
     * Deletes this table's tfoot element. If the table has multiple tfoot elements, this
     * method deletes only the first tfoot element. If this table does not have any tfoot
     * elements, this method does nothing.
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/methods/deletecaption.asp">
     * MSDN Documentation</a>
     */
    public void jsxFunction_deleteTFoot() {
        getHtmlElementOrDie().removeChild("tfoot", 0);
    }

    /**
     * Deletes this table's thead element. If the table has multiple thead elements, this
     * method deletes only the first thead element. If this table does not have any thead
     * elements, this method does nothing.
     * @see <a href="http://msdn.microsoft.com/workshop/author/dhtml/reference/methods/deletethead.asp">
     * MSDN Documentation</a>
     */
    public void jsxFunction_deleteTHead() {
        getHtmlElementOrDie().removeChild("thead", 0);
    }

    /**
     * Refreshes the content of this table.
     * @see <a href="http://msdn2.microsoft.com/en-us/library/ms536687.aspx">
     * MSDN Documentation</a>
     */
    public void jsxFunction_refresh() {
        // Empty: this method only affects rendering, which we don't care about.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getXPathRows() {
        return "./node()/tr";
    }
  
    /**
     * Handle special case where table is empty.
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected Object insertRow(final int index) {
        // check if a tbody should be created
        final List< ? extends HtmlElement> rowContainers = (List< ? extends HtmlElement>)
            getHtmlElementOrDie().getByXPath("//tbody | //thead | //tfoot");
        if (rowContainers.isEmpty() || index == 0) {
            final HtmlElement tBody = getHtmlElementOrDie().appendChildIfNoneExists("tbody");
            return ((RowContainer) getScriptableFor(tBody)).insertRow(0);
        }
        return super.insertRow(index);
    }

    /**
     * Returns the <tt>width</tt> attribute.
     * @return the <tt>width</tt> attribute
     */
    public String jsxGet_width() {
        return getHtmlElementOrDie().getAttributeValue("width");
    }
    
    /**
     * Sets the <tt>width</tt> attribute.
     * @param width the <tt>width</tt> attribute
     */
    public void jsxSet_width(final String width) {
        getHtmlElementOrDie().setAttributeValue("width", width);
    }

    /**
     * Returns the <tt>cellSpacing</tt> attribute.
     * @return the <tt>cellSpacing</tt> attribute
     */
    public String jsxGet_cellSpacing() {
        return getHtmlElementOrDie().getAttributeValue("cellspacing");
    }
    
    /**
     * Sets the <tt>cellSpacing</tt> attribute.
     * @param cellSpacing the <tt>cellSpacing</tt> attribute
     */
    public void jsxSet_cellSpacing(final String cellSpacing) {
        getHtmlElementOrDie().setAttributeValue("cellspacing", cellSpacing);
    }

    /**
     * Returns the <tt>cellPadding</tt> attribute.
     * @return the <tt>cellPadding</tt> attribute
     */
    public String jsxGet_cellPadding() {
        return getHtmlElementOrDie().getAttributeValue("cellpadding");
    }
    
    /**
     * Sets the <tt>cellPadding</tt> attribute.
     * @param cellPadding the <tt>cellPadding</tt> attribute
     */
    public void jsxSet_cellPadding(final String cellPadding) {
        getHtmlElementOrDie().setAttributeValue("cellpadding", cellPadding);
    }
}
