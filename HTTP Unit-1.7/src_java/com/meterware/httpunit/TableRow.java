package com.meterware.httpunit;
import org.w3c.dom.html.HTMLTableCellElement;
import org.w3c.dom.html.HTMLTableRowElement;

import java.util.ArrayList;

import com.meterware.httpunit.scripting.ScriptableDelegate;

/**
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 */
public class TableRow extends HTMLElementBase {

    private ArrayList _cells = new ArrayList();
    private WebTable _webTable;
    private HTMLTableRowElement _element;


    TableRow( WebTable webTable, HTMLTableRowElement element ) {
        super( element );
        _element = element;
        _webTable = webTable;
    }


    TableCell[] getCells() {
        
        return (TableCell[]) _cells.toArray( new TableCell[ _cells.size() ]);
    }


    TableCell newTableCell( HTMLTableCellElement element ) {
        return _webTable.newTableCell( element );
    }


    void addTableCell( TableCell cell ) {
        _cells.add( cell );
    }


    @Override
    public ScriptableDelegate newScriptable() {
        return new HTMLElementScriptable( this );
    }


    public ScriptableDelegate getParentDelegate() {
        return _webTable.getParentDelegate();
    }
}
