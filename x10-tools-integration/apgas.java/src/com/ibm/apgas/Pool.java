package com.ibm.apgas;

import x10.lang.Place;


@SuppressWarnings("serial")
public class Pool extends x10.runtime.impl.java.Runtime {
    Task mainTask;
    
    public Pool(Task task) {
        mainTask = task;
    }
  
    public void start() {
        System.setProperty("x10.NO_PRELOAD_CLASSES", "true");
        start(new String[]{});
    }
    
    // called by native runtime inside main x10 thread
    public void runtimeCallback(final x10.array.Array<java.lang.String> args) {
        mainTask.body();
    }
    
    public static void runAsync(Task task) {
        x10.lang.Runtime.runAsync(task);                
    }
    
    public static void runAsync(int place, Task task) {
        Place p =  x10.lang.Place.place(place);
        x10.lang.Runtime.runAsync(p, task);
    }

    public static void runFinish(Task task) {
        x10.lang.Runtime.runFinish(task);
    }
    
    public static void atEach(final Task task) {
        runFinish(new Task() {
            public void body() {
                for (int i=0; i<numPlaces(); i++) {
                    if (i != here()) runAsync(i, task);
                 }
                task.body();
            }
        });        
    }
    
    public static int numPlaces() { 
        return x10.lang.Place.numPlaces$O();
    }
    
    public static int here() {
        return x10.lang.Runtime.hereInt$O();
    }
}