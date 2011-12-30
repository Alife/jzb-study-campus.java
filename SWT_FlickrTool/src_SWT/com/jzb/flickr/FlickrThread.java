/**
 * 
 */
package com.jzb.flickr;

import java.io.File;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.ArrayList;

import com.jzb.flickr.act.FlickrContext;
import com.jzb.flickr.xmlbean.ActTaskManager;
import com.jzb.flickr.xmlbean.IActTask;
import com.jzb.flickr.xmlbean.IActTaskManager;
import com.jzb.flickr.xmlbean.ITracer;
import com.jzb.flickr.xmlbean.XMLActParser;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("deprecation")
public class FlickrThread {

    public static final int                  NUM_THREADS = 10;

    ArrayList<IActTask>                      m_actTaskList;
    IActTaskManager                          m_manager;
    private IActTaskManager.IProgressMonitor m_monitor;
    private ITracer                          m_traces;

    FlickrThread(String doneTasksLogFolder, IActTaskManager.IProgressMonitor monitor, ITracer traces) {
        try {
            m_monitor = monitor;
            m_traces = traces;
            m_manager = new ActTaskManager(doneTasksLogFolder, m_traces, m_monitor, NUM_THREADS);

            File file = new File(doneTasksLogFolder);
            if (file.exists() && file.isDirectory()) {
                if (file.listFiles().length > 0) {
                    m_traces._warn("FlickrThread: Already executed tasks log folder contains data of previous executions!");
                }
            }

        } catch (Exception ex) {
            m_traces._error("FlickrThread: Error starting processing thread", ex);
            m_monitor.processingEnded(false);
        }
    }

    public void cancel() {
        m_manager.abortProcessing();
    }

    public void start(final String script) {

        try {
            if (script != null && script.length() > 0) {
                InputStream is = new StringBufferInputStream(script);
                m_actTaskList = XMLActParser.parse(m_traces, is);
                is.close();

                new Thread(new Runnable() {

                    @SuppressWarnings("synthetic-access")
                    public void run() {
                        runThis();
                    }
                }).start();
            } else {
                m_traces._info("FlickrThread: There is no script to execute (you can use the examples)!");
                m_monitor.processingEnded(false);
            }
        } catch (Exception ex) {
            m_traces._error("FlickrThread: Error starting processing thread", ex);
            m_monitor.processingEnded(false);
        }

    }


    private void runThis() {

        try {
            m_traces._debug("FlickrThread: Starting processing...");
            FlickrContext.init();
            FlickrContext.setHasFailed(false);

            for (IActTask actTask : m_actTaskList) {
                actTask.prepareExecution(null, null);
                m_manager.submitActTask(actTask);
            }
            m_traces._debug("FlickrThread: Waiting for all tasks to finish...");
            m_manager.waitForFinishing();

            m_traces._debug("FlickrThread: Processing finished!");


        } catch (Exception ex) {
            m_traces._error("FlickrThread: Error executing task processing",ex);
        }
        
        m_monitor.processingEnded(FlickrContext.hasFailed());
        
    }
}
