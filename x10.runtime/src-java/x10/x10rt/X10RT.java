/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.x10rt;

public class X10RT {
    private enum State { UNINITIALIZED, BOOTED, TEARING_DOWN, TORN_DOWN };

    private static State state = State.UNINITIALIZED;
    private static Place[] places;
    private static Place here;
    private static int numProgressThreads;

    static final boolean REPORT_UNCAUGHT_USER_EXCEPTIONS = true;

    /**
     * Initialize the X10RT runtime.  This method must be called before any other
     * methods on this class or on any other X10RT related class can be successfully
     * invoked.
     *
     * @param nProgressThreads The number of progress threads to create to process
     *                           incoming messages.
     * @throws IllegalArgumentException if numProgressThreads is not positive
     */
    static {
      assert state.compareTo(State.TEARING_DOWN) < 0 : "X10RT is shutting down";
      assert state != State.BOOTED : "X10RT is already booted!";

      String libName = System.getProperty("X10RT_IMPL", "x10rt_pgas_sockets");
      System.loadLibrary(libName);

      // TODO: For now we are not trying to plumb the command line arguments from
      //       the program's main method into X10RT.  We really can't easily do this
      //       unless we change this code to be run via an explicit static method in
      //       X10RT instead of doing it in the class initializer.  
      //       Consider whether or not we should make this change....
      x10rt_init(0, null);

      ActiveMessage.initializeMessageHandlers();

      places = new Place[x10rt_nplaces()];
      for (int i=0; i<places.length; i++) {
        places[i] = new Place(i);
      }
      here = places[x10rt_here()];

      // Add a shutdown hook to automatically teardown X10RT as part of JVM teardown
      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
        public void run() {
          synchronized(X10RT.class) {
            state = State.TEARING_DOWN;
            x10rt_finalize();
            state = State.TORN_DOWN;
            System.err.flush();
            System.out.flush();
          }
        }}));

      // Create thread dedicated to poll/drain the message queue.
      for (int i = 0; i<numProgressThreads; i++) {
          Thread progressThread = new Thread(new Runnable(){
              public void run() {
                  while (true) {
                      try {
                          X10RT.probe();
                      } catch (Throwable e) {
                          if (REPORT_UNCAUGHT_USER_EXCEPTIONS) {
                              e.printStackTrace();
                          }
                      }
                      Thread.yield();
                  }
              }}, "X10RT Progress Thread #"+i);
          progressThread.setDaemon(true);
          progressThread.start();
      }

      state = State.BOOTED;
    }

    /**
     * This is a blocking call.
     * All places must participate, and nobody returns from the call
     * until every place has entered.
     */
    public static void barrier() {
      assert state.compareTo(State.BOOTED) >= 0;
      x10rt_barrier();
    }

    /**
     * This is a blocking call.
     * Returns when all outstanding communication operations on this Place locally complete.
     */
    public static void fence() {
      assert state.compareTo(State.BOOTED) >= 0;
      x10rt_remote_op_fence();
    }

    /**
     * This is a non-blocking call.
     * Checks network for incoming messages and returns.
     */
    public static void probe() {
        assert state.compareTo(State.BOOTED) >= 0;
        x10rt_probe();
    }

    /**
     * Get the Place object that represents the Place where this process is executing.
     * @return the Place object that represents the Place where this process is executing.
     */
    public static Place here() {
      assert state.compareTo(State.BOOTED) >= 0;
      return here;
    }

    /**
     * Return the number of places in the computation.
     * @return the number of places in the computation.
     */
    public static int numPlaces() {
      assert state.compareTo(State.BOOTED) >= 0;
      return places.length;
    }

    /**
     * Get the Place object that represents Place placeId
     * @param placeId the numeric id for the desired place.
     * @return the Place object that represents placeId
     * @throws IllegalArgumentException if placeId is not valid.
     */
    public static Place getPlace(int placeId) throws IllegalArgumentException {
      assert state.compareTo(State.BOOTED) >= 0;

      try {
        return places[placeId];
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new IllegalArgumentException("Invalid place id "+placeId);
      }
    }

    static boolean isBooted() {
      return state.compareTo(State.BOOTED) >= 0;
    }

    /*
     * Native methods exported from x10rt_front.h that are 
     * related to initialization and finalization of the X10RT library.
     * See X10RT API at x10-lang.org for semantics. 
     */
    private static native int x10rt_init(int numArgs, String[] args);
    
    private static native int x10rt_finalize();
    
    /*
     * Native method exported from x10rt_front.h that are related to Places
     */
    private static native int x10rt_nplaces();
    
    private static native int x10rt_nhosts();
    
    private static native int x10rt_here();
    
    private static native boolean x10rt_is_host(int place);
    
    private static native boolean x10rt_is_cuda(int place);
    
    private static native boolean x10rt_is_spe(int place);
    
    private static native int x10rt_parent(int place);
    
    private static native int x10rt_nchildren(int place);
    
    private static native int x10rt_child(int host, int index);
    
    private static native int x10rt_child_index(int child);
    
    /*
     * Subset of x10rt_front.h API related to messages that actually needs
     * to be exposed at the Java level (as opposed to being used
     * in the native code backing the native messages of ActiveMessage.
     */
    private static native void x10rt_probe();
    
    private static native void x10rt_remote_op_fence();
    
    private static native void x10rt_barrier();
}
