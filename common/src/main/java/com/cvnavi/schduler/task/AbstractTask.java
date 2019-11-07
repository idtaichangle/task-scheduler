package com.cvnavi.schduler.task;

public interface AbstractTask extends Runnable{
    void newDayBegin();
    void interruptTask();
    boolean timeToFire(long time);
}
