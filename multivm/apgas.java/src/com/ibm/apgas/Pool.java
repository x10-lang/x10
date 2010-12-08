package com.ibm.apgas;


@SuppressWarnings("serial")
public class Pool extends x10.runtime.impl.java.Runtime {
    Runnable mainTask;
    
    public Pool(Runnable task) {
        mainTask = task;
    }
  
    public void start() {
        System.setProperty("x10.NO_PRELOAD_CLASSES", "true");
        start(new String[]{});
    }
    
    // called by native runtime inside main x10 thread
    public void runtimeCallback(final x10.array.Array<java.lang.String> args) {
        mainTask.run();
    }
    
    public void runAsync(Runnable task) {
        Task t = new Task(task);
        x10.lang.Runtime.runAsync(t);
    }
                                          
}