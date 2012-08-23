/**
 * 
 */
package com.jzb.img.ui;

import java.io.File;

import com.jzb.img.tsk.BaseTask.JustCheck;
import com.jzb.img.tsk.BaseTask.RecursiveProcessing;

/**
 * @author jzarzuela
 * 
 */
public interface ITaskWnd {

    public JustCheck getJustCheck();

    public RecursiveProcessing getRecursiveProcessing();

    public File getBaseFolder();
    
    public void runTask(String taskName, Runnable task);

}
