package com.ibm.apgas;

import x10.lang.Place;

/**
 * A Pool wraps the X10 runtime and provides a simple
 * Java-level library API for executing tasks using
 * the Asynchronous Partitioned Global Address Space 
 * programming model.  Programmers can create Tasks
 * representing the bodies of async/at/finish constructs
 * and submit them to the pool for execution.
 */
@SuppressWarnings("serial")
public class Pool extends x10.runtime.impl.java.Runtime {
    Task mainTask;
    
    /**
     * Create a pool and specify the main task that the pool should execute.
     * Note that this does not begin executing the argument Task.  Execution 
     * does not start until the start method is called.
     * @param task
     */
    public Pool(Task task) {
        mainTask = task;
    }
  
    /**
     * Initialize the runtime system and start executing the mainTask of the pool.
     */
    public void start() {    	
    	// We have to force early loading of com.ibm.apgas.TaskWrapper to ensure
    	// it gets a deserialization id before the x10rt message ids are finalized by start.
        System.setProperty("x10.NO_PRELOAD_CLASSES", "false");
        String preload = System.getProperty("x10.EXTRA_PRELOAD_CLASSES");
        preload = ((null == preload) ? "" : ",")+com.ibm.apgas.TaskWrapper.class.getName();
        System.setProperty("x10.EXTRA_PRELOAD_CLASSES", preload);
    	
        start(new String[]{});
    }
    
    // called by native runtime inside main x10 thread.
    // Should not be called by usercode.  
    // Only made public to conform to superclass API.  Really should be protected. 
    public void runtimeCallback(final x10.array.Array<java.lang.String> args) {
        mainTask.body();
    }
    
    /**
     * Schedule the argument task as an async to be executed in the current place.
     * @param task The task to execute. 
     */
    public static void runAsync(Task task) {   	
        x10.lang.Runtime.runAsync(new TaskWrapper(task));                
    }
    
    /**
     * Schedule the arugment task as an async to be executed in the argument place.
     * @param place
     * @param task
     */
    public static void runAsync(int place, Task task) {
        Place p =  x10.lang.Place.place(place);
        x10.lang.Runtime.runAsync(p, new TaskWrapper(task));
    }

    /**
     * Execute the body of the argument task as the body of a Finish statement.
     * @param task
     */
    public static void runFinish(Task task) {
        x10.lang.Runtime.runFinish(new TaskWrapper(task));
    }
    
    /**
     * Inside a new Finish scope, execute the argument task at all places.
     * @param task
     */
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
    
    /**
     * How many places are there in the current execution?
     */
    public static int numPlaces() { 
        return x10.lang.Place.numPlaces$O();
    }
    
    /**
     * What is the numeric id of the current place?
     */
    public static int here() {
        return x10.lang.Runtime.hereInt$O();
    }
}