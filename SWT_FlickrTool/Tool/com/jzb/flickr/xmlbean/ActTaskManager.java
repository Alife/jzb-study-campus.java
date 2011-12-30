/**
 * 
 */
package com.jzb.flickr.xmlbean;

import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.jzb.flickr.act.FlickrContext;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("synthetic-access")
public class ActTaskManager implements IActTaskManager {

    private class TaskWrapper implements Runnable {

        public static final int STATUS_DONE   = 1;
        public static final int STATUS_FAILED = 2;
        public static final int STATUS_READY  = 0;
        public static final int STATUS_RETRY  = 3;
        // public static final String STATUS_TXT[] = { "0-READY", "1-DONE", "2-FAILED", "3-RETRY" };

        public IActTask         m_actTask;
        public int              m_msToExecute;
        public String           resultText;

        public int              retries;

        public int              status;

        public TaskWrapper(IActTask actTask) {
            m_actTask = actTask;
            status = STATUS_READY;
            resultText = "";
            retries = 0;
        }

        public void prepareToRetry() {
            status = STATUS_READY;
            resultText = "RETRYING";
            m_msToExecute = 0;
        }

        public void run() {

            long t1 = System.currentTimeMillis();

            File lock = null;
            try {
                lock = _chechLock();
                if (lock != null) {
                    m_actTask.execute();
                    status = STATUS_DONE;
                    resultText = "OK";
                } else {
                    m_tracer._warn(" *** Skiping task execution (" + m_actTask.getActionSignature() + ")!! Lock ID: " + lock);
                }
            } catch (Throwable ex) {
                _deleteLock(lock);

                if (m_actTask.canRetry()) {
                    status = STATUS_RETRY;
                } else {
                    status = STATUS_FAILED;
                    FlickrContext.setHasFailed(true);
                }

                resultText = "Error (" + ex.getClass().getName() + "): " + ex.getMessage();
                retries++;
                m_tracer._error(" *** Error in task execution (" + m_actTask.getActionSignature() + ")!! Error: " + resultText);

            }

            long t2 = System.currentTimeMillis();
            m_msToExecute = (int) (t2 - t1);

            _taskExecFinihed(this);
        }

        private File _chechLock() throws Exception {
            if (m_actTask.canReexecute()) {
                return UNLOCKED_FILE;
            } else {
                String signature = m_actTask.getActionSignature();
                String md5 = getMD5(signature);
                File lck = new File(m_doneTasksFolder, md5 + ".tsk_lck");
                if (!lck.exists()) {
                    FileWriter fw = new FileWriter(lck);
                    fw.append(signature);
                    fw.close();
                    return lck;
                } else {
                    return null;
                }
            }
        }

        private void _deleteLock(File lock) {
            try {
                if (!UNLOCKED_FILE.equals(lock))
                    lock.delete();
            } catch (Throwable ex) {
            }
        }

        private String getMD5(String msg) throws Exception {

            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(msg.getBytes());
            byte buffer[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < buffer.length; i++) {
                hexString.append(Integer.toHexString(0xFF & buffer[i]));
            }
            return hexString.toString();
        }
    }

    private static final int  MAX_TASK_RETRIES = 3;

    private static final File UNLOCKED_FILE    = new File("");
    private volatile boolean  m_aborted;
    private File              m_doneTasksFolder;
    private ExecutorService   m_execSrvc;
    private IProgressMonitor  m_pMonitor;
    private AtomicInteger m_tasksCount = new AtomicInteger(0);

    private ITracer           m_tracer;

    public ActTaskManager(String doneTasksFolder, ITracer tracer, IProgressMonitor pMonitor, int numThreads) throws Exception {

        m_tracer = tracer;
        m_doneTasksFolder = new File(doneTasksFolder);
        if (!m_doneTasksFolder.exists() && !m_doneTasksFolder.mkdirs()) {
            throw new Exception("Error creating task_lock folder: " + m_doneTasksFolder);
        }
        m_tracer._info("Created task_lock folder: " + m_doneTasksFolder.getAbsolutePath());

        if (pMonitor != null)
            m_pMonitor = pMonitor;
        else
            m_pMonitor = new SysOutProgressMonitor(tracer);

        m_execSrvc = Executors.newFixedThreadPool(numThreads);
    }

    public void abortProcessing() {
        m_tracer._debug("Aborting task processing...");
        m_execSrvc.shutdownNow();
        m_aborted = true;

    }

    public void submitActTask(IActTask actTask) {
        actTask.setActTaskManager(this);
        _submitTask(new TaskWrapper(actTask));
    }

    public void waitForFinishing() throws Exception {
        m_execSrvc.awaitTermination(1000, TimeUnit.DAYS);
    }

    private void _submitTask(TaskWrapper wrapper) {

        if (m_aborted)
            return;

        try {
            int n = m_tasksCount.incrementAndGet();
            m_execSrvc.execute(wrapper);
            m_pMonitor.newTaskAdded(wrapper.m_actTask, n);
        } catch (Throwable ex) {
            m_tasksCount.decrementAndGet();
        }

    }

    private void _taskExecFinihed(TaskWrapper wrapper) {

        int lastTS = wrapper.m_msToExecute; // Se pone a cero si reintenta
        if (wrapper.status == TaskWrapper.STATUS_RETRY && wrapper.retries < MAX_TASK_RETRIES) {
            wrapper.prepareToRetry();
            _submitTask(wrapper);
        }

        int n = m_tasksCount.decrementAndGet();

        m_pMonitor.taskEnded(wrapper.m_actTask, lastTS, n);

        if (n <= 0) {
            m_tracer._debug("Task processing finished!");
            m_execSrvc.shutdown();
        }

    }

}
