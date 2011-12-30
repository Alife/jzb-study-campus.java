/**
 * 
 */
package com.jzb.flickr.xmlbean;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

/**
 * @author n000013
 * 
 */
@SuppressWarnings("unchecked")
public abstract class TaskActBase implements IActTask {

    private ActionBuilder       m_actBuilder;
    private IAction             m_actToExec;
    private ArrayList<IActTask> m_innerPrototypes;
    private IActTaskManager     m_manager;
    private ITracer             m_tracer;

    public TaskActBase(ITracer tracer) {
        m_tracer = tracer;
    }

    /**
     * @see com.jzb.flickr.xmlbean.IActTask#canReexecute()
     */
    public boolean canReexecute() {
        return m_actToExec.canReexecute();
    }

    public boolean canRetry() {
        return m_actToExec.canRetry();
    }

    @Override
    public IActTask clone() throws CloneNotSupportedException {
        try {
            TaskActBase clon;
            Constructor ctor = getClass().getConstructor(ITracer.class);
            clon = (TaskActBase) ctor.newInstance(m_tracer);
            clon.m_actBuilder = m_actBuilder;
            clon.m_innerPrototypes = m_innerPrototypes;
            clon.m_manager = m_manager;
            clon.m_actToExec = null;
            return clon;
        } catch (Exception ex) {
            throw new CloneNotSupportedException("Error creating a clon: " + ex.getClass().getName() + " - " + ex.getMessage());
        }
    }

    public void execute() throws Exception {

        if (m_actToExec == null) {
            throw new NullPointerException("You must call 'prepareExecution' before executing it");
        }

        Object result = m_actToExec.execute();

        if (result != null) {
            if (result instanceof List) {
                List actions = (List) result;
                for (Object element : actions) {
                    execSubTasks(element);
                }
            } else {
                execSubTasks(result);
            }
        }

    }

    public String getActionName() {
        return m_actToExec.getClass().getName();
    }

    public String getActionSignature() {
        return m_actToExec.getSignature();
    }

    public void initialize(Node node) throws Exception {
        m_actBuilder = new ActionBuilder(m_tracer, node);
        m_innerPrototypes = XMLActParser.parseNodes(m_tracer, node);
    }

    public void prepareExecution(IAction parentAct, Object parentData) throws Exception {
        m_actToExec = m_actBuilder.newInstance(parentAct, parentData);
    }

    public void setActTaskManager(IActTaskManager manager) {
        m_manager = manager;
    }

    protected void execSubTasks(Object item) throws Exception {

        for (IActTask prototype : m_innerPrototypes) {
            IActTask subTask = prototype.clone();
            subTask.prepareExecution(m_actToExec, item);
            if (m_manager == null)
                subTask.execute();
            else
                m_manager.submitActTask(subTask);
        }
    }

    /**
     * @return the actBuilder
     */
    protected ActionBuilder getActBuilder() {
        return m_actBuilder;
    }

}
