/**
 * 
 */
package com.jzb.img.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * @author jzarzuela
 * 
 */
public abstract class BaseUI extends Composite {

    private ITaskWnd m_taskWnd;

    // --------------------------------------------------------------------------------------------------------
    public BaseUI(Composite parent, int style) {
        super(parent, style);
    }

    // --------------------------------------------------------------------------------------------------------
    public abstract String getTaskDescription();

    // --------------------------------------------------------------------------------------------------------
    public abstract String getTaskName();

    // --------------------------------------------------------------------------------------------------------
    public ITaskWnd getTaskWnd() {
        return m_taskWnd;
    }

    // --------------------------------------------------------------------------------------------------------
    public void setTaskWnd(ITaskWnd taskWnd) {
        m_taskWnd = taskWnd;
    }

    // --------------------------------------------------------------------------------------------------------
    protected int _parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfex) {
            return 0;
        }
    }

}
