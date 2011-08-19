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
    private static int here;
    private static int numPlaces;
    static boolean forceSinglePlace = false;

    static final boolean REPORT_UNCAUGHT_USER_EXCEPTIONS = true;
    
    public static final boolean VERBOSE = false;

    // TODO: We would like to avoid doing this via a clinit method.
    //       But right now, the clinit method of x10.runtime.impl.java.Runtime
    //       references X10RT, so we have to do it this way...
    static {
    	init();
    }
    
    /**
     * Initialize the X10RT runtime.  This method must be called before any other
     * methods on this class or on any other X10RT related class can be successfully
     * invoked.
     * 
     * @throws IllegalArgumentException if numProgressThreads is not positive
     */
    public static synchronized void init() {
      if (state != State.UNINITIALIZED) return;

      String libName = System.getProperty("X10RT_IMPL", "x10rt_sockets");
      if (libName.equals("disabled")) {
          forceSinglePlace = true;
      } else {
          try {
              System.loadLibrary(libName);
          } catch (UnsatisfiedLinkError e) {
              System.err.println("Unable to load "+libName+". Forcing single place execution");
              forceSinglePlace = true;
          }
      }

      if (forceSinglePlace) {
          here = 0;
          numPlaces = 1;
      } else {
          // TODO: For now we are not trying to plumb the command line arguments from
          //       the program's main method into X10RT.  We really can't easily do this
          //       until we change this code to be run via an explicit static method in
          //       X10RT instead of doing it in the class initializer.  

          x10rt_init(0, null);

          TeamSupport.initialize();

          here = x10rt_here();
          numPlaces = x10rt_nplaces();

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
      }

      state = State.BOOTED;
    }

    /**
     * This is a non-blocking call.
     * Checks network for incoming messages and returns.
     */
    public static void probe() {
        assert isBooted();
        if (!forceSinglePlace) x10rt_probe();
    }

    /**
     * Return the numeric id of the current Place.
     * @return the numeric id of the current Place.
     */
    public static int here() {
      assert isBooted();
      return here;
    }

    /**
     * Return the number of places in the computation.
     * @return the number of places in the computation.
     */
    public static int numPlaces() {
      assert isBooted();
      return numPlaces;
    }

    static boolean isBooted() {
      return state.compareTo(State.BOOTED) >= 0;
    }

    /**
     * To be called once XRX is ready to process incoming asyncs.
     */
    public static void registration_complete() {
        if (!forceSinglePlace) x10rt_registration_complete();
    }

    /*
     * Native methods exported from x10rt_front.h that are 
     * related to initialization and finalization of the X10RT library.
     * See X10RT API at x10-lang.org for semantics. 
     */
    private static native int x10rt_init(int numArgs, String[] args);
    
    private static native int x10rt_finalize();

    private static native int x10rt_registration_complete();

    /*
     * Native method exported from x10rt_front.h that are related to Places
     */
    private static native int x10rt_nplaces();
        
    private static native int x10rt_here();
    
    /*
     * Subset of x10rt_front.h API related to messages that actually needs
     * to be exposed at the Java level (as opposed to being used
     * in the native code backing the native methods of MessageHandlers.
     */
    private static native void x10rt_probe();
    
}
