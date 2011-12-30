package com.jzb.flow;

public interface IState {

    public void setEventListener(IEventListener listener);

    public String getName();

    public void activate();
    
    public void signalException(Throwable th);

}
