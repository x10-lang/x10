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

import x10.lang.GlobalRail;
import x10.x10rt.SocketTransport.RETURNCODE;

public class X10RT {
    private enum State { UNINITIALIZED, INITIALIZED, RUNNING, TEARING_DOWN, TORN_DOWN };

    private static State state = State.UNINITIALIZED;
    private static int here;
    private static int numPlaces;
    static boolean forceSinglePlace = false;
    public static SocketTransport javaSockets = null;
    
    public static boolean X10_EXITING_NORMALLY = false;
    static final boolean REPORT_UNCAUGHT_USER_EXCEPTIONS = true;
    public static final boolean VERBOSE = false;
    
    /**
     * Initialize the X10RT runtime.  This method, or the standard init() method below 
     * must be called before any other methods on this class or on any other X10RT 
     * related class can be successfully invoked.
     */
    public static synchronized String init_library() {
    	if (state != State.UNINITIALIZED && 
    			state != State.TORN_DOWN) return null; // already initialized

        // load libraries
        String property = System.getProperty("x10.LOAD");
        if (null != property) {
            String[] libs = property.split(":");
            for (int i = libs.length - 1; i >= 0; i--)
                System.loadLibrary(libs[i]);
        }

        String libName = System.getProperty("X10RT_IMPL", "sockets");
        if (libName.equals("disabled"))
            forceSinglePlace = true;
        else if (libName.equalsIgnoreCase("JavaSockets")) {
      	  	X10RT.javaSockets = new SocketTransport();
      	    state = State.INITIALIZED;
      	  	return X10RT.javaSockets.getLocalConnectionInfo();
        }
        else {
            libName = "x10rt_" + libName;
            try {
                System.loadLibrary(libName);
            } catch (UnsatisfiedLinkError e) {
                System.err.println("Unable to load "+libName+". Forcing single place execution");
                forceSinglePlace = true;
            }
        }
        
/*    	x10.lang.Runtime.get$staticMonitor();
        x10.lang.Runtime.get$STRICT_FINISH();
        x10.lang.Runtime.get$NTHREADS();
        x10.lang.Runtime.get$MAX_THREADS();
        x10.lang.Runtime.get$STATIC_THREADS();
        x10.lang.Runtime.get$WARN_ON_THREAD_CREATION();
        x10.lang.Runtime.get$BUSY_WAITING();
*/

        state = State.INITIALIZED;
        if (forceSinglePlace) {
        	here = 0;
        	numPlaces = 1;
        	x10.runtime.impl.java.Runtime.MAX_PLACES = numPlaces;
            state = State.RUNNING;
        	return null;
        }
        else
        	return x10rt_preinit();
    }
    
    /*
     * This method is the second phase of the init_library() call above.  Init_library only initializes
     * internal variables, minus what is needed for communication with other places.
     * 
     * myPlace is which place this runtime is in the whole computation.
     * connectionInfo is an array the size of nplaces, and contains the connection string for each
     * remote place.  The connection string for myPlace may be null.
     * 
     * This method returns true if the runtime was successfully initialized.
     * If false is returned, the caller should call this method again until true is returned.
     */
    public static synchronized boolean connect_library(int myPlace, String[] connectionInfo, boolean remoteStart) {
    	if (state != State.INITIALIZED) return true; // already initialized

        X10RT.here = myPlace;
        if (connectionInfo == null)
        	numPlaces = 1;
        else
        	numPlaces = connectionInfo.length;
    
    	int errcode;
    	if (X10RT.javaSockets != null)
    		errcode = X10RT.javaSockets.establishLinks(myPlace, connectionInfo, remoteStart);
    	else {
    		errcode = x10rt_init(myPlace, connectionInfo);
    		TeamSupport.initialize();
    	}
        if (errcode != 0) {
            System.err.println("Failed to initialize X10RT.");
            x10rt_finalize();
            return false;
        }
        x10.runtime.impl.java.Runtime.MAX_PLACES = numPlaces;
        state = State.RUNNING;
        return true;
    }

    
    /*
     * This method returns true if the runtime was successfully initialized.
     * If false is returned, the caller should call this method again until true is returned.
     */
    public static synchronized boolean init() {
      if (state != State.UNINITIALIZED) return true; // already initialized

      String libName = System.getProperty("X10RT_IMPL", "sockets");
      if (libName.equals("disabled")) {
          forceSinglePlace = true;
      } 
      else if (libName.equalsIgnoreCase("JavaSockets")) {
    	  X10RT.javaSockets = new SocketTransport();
    	  int ret = X10RT.javaSockets.establishLinks();
    	  if (ret != RETURNCODE.X10RT_ERR_OK.ordinal()) {
    		  forceSinglePlace = true;
    		  System.err.println("Unable to establish links!  errorcode: "+ret+". Forcing single place execution");
    	  }
    	  else {
    		  here = X10RT.javaSockets.x10rt_here();
    		  numPlaces = X10RT.javaSockets.x10rt_nplaces();
    	  }
      }
      else {
          libName = "x10rt_" + libName;
          try {
              System.loadLibrary(libName);
              int err = x10rt_init(0, null);
              if (err != 0) {
//                  System.err.println("Failed to initialize X10RT.");
                  x10rt_finalize();
                  return false;
              }

              TeamSupport.initialize();

              here = x10rt_here();
              numPlaces = x10rt_nplaces();
          } catch (UnsatisfiedLinkError e) {
              System.err.println("Unable to load "+libName+". Forcing single place execution");
              forceSinglePlace = true;
          }
      }

      if (forceSinglePlace) {
          here = 0;
          numPlaces = 1;
      }
      else {
          // Add a shutdown hook to automatically teardown X10RT as part of JVM teardown
          Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
              public void run() {
                  synchronized(X10RT.class) {
                      state = State.TEARING_DOWN;
                      if (X10_EXITING_NORMALLY) {
                          if (VERBOSE) System.err.println("Normal exit; x10rt_finalize called");
                          if (javaSockets != null)
                        	  javaSockets.shutdown();
                          else
                        	  x10rt_finalize();
                          if (VERBOSE) System.err.println("Normal exit; x10rt_finalize returned");
                      } else {
                          if (VERBOSE) System.err.println("Abnormal exit; skipping call to x10rt_finalize");
                      }
                      state = State.TORN_DOWN;
                      System.err.flush();
                      System.out.flush();
                  }
              }}));
      }
      x10.runtime.impl.java.Runtime.MAX_PLACES = numPlaces;
      state = State.RUNNING;
      return true;
    }

    /**
     * This is a non-blocking call.
     * Checks network for incoming messages and returns.
     */
    public static int probe() {
        assert isBooted();
        if (javaSockets != null)
        	return javaSockets.x10rt_probe();
        else if (!forceSinglePlace)
        	return x10rt_probe();
        else
        	return 0;
    }

    /**
     * This is a blocking call.
     */
    public static int blockingProbe() {
        assert isBooted();
        if (javaSockets != null)
        	return javaSockets.x10rt_blocking_probe();
        else if (!forceSinglePlace)
        	return x10rt_blocking_probe();
        else 
        	return 0;
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

    /**
     * Return the number of dead places.
     * @return the number of dead places.
     */
    public static int numDead() {
    	assert isBooted();
    	if (javaSockets != null) 
    		return javaSockets.numDead();
    	else if (!forceSinglePlace) 
    		return x10rt_ndead();
    	else
    		return 0;
    }

    /**
     * Returns true if the place is dead.
     * @return true if the place is dead.
     */
    public static boolean isPlaceDead(int place) {
    	assert isBooted();
    	if (javaSockets != null) 
    		return javaSockets.isPlaceDead(place);
    	else if (!forceSinglePlace) 
    		return x10rt_is_place_dead(place);
    	else
    		return false;
    }

    public static boolean supportsCollectives() {
        assert isBooted();
        if (forceSinglePlace || javaSockets != null)
        	return false;
        else
        	// at this point, the emulated collectives are still available in native code.
        	// TODO: benchmark emulated collectives in X10 vs native, and drop native if it's slower.
        	return true;
      }

    static boolean isBooted() {
      return state.compareTo(State.RUNNING) >= 0;
    }

    /**
     * To be called once XRX is ready to process incoming asyncs.
     */
    public static void registration_complete() {
        if (!forceSinglePlace && javaSockets == null)
        	x10rt_registration_complete();
    }
    
    public static void registerHandlers() {
    	if (!forceSinglePlace && javaSockets == null)
    		x10.x10rt.MessageHandlers.registerHandlers();
    }
    
    // library-mode alternative to the shutdown hook in init()
    public static synchronized int disconnect() {
    	state = State.TEARING_DOWN;
    	int ret = 0;
    	if (javaSockets != null)
    		ret = javaSockets.shutdown();
    	else
    		ret = x10rt_finalize();
    	state = State.TORN_DOWN;
    	return ret;
    }
    
    /*
     * Support for remote operations
     */
    public static void remoteAdd(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteAdd not implemented for Managed X10");
    }
    public static void remoteAdd__1$u(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteAdd not implemented for Managed X10");
    }

    public static void remoteAnd(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteAnd not implemented for Managed X10");
    }
    public static void remoteAnd__1$u(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteAnd not implemented for Managed X10");
    }

    public static void remoteOr(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteOr not implemented for Managed X10");
    }
    public static void remoteOr__1$u(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteOr not implemented for Managed X10");
    }

    public static void remoteXor(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteXor not implemented for Managed X10");
    }
    public static void remoteXor__1$u(GlobalRail target, long idx, long val) {
        throw new UnsupportedOperationException("remoteXor not implemented for Managed X10");
    }

    /*
     * Native methods exported from x10rt_front.h that are 
     * related to initialization and finalization of the X10RT library.
     * See X10RT API at x10-lang.org for semantics. 
     */
    private static native String x10rt_preinit();
    
    private static native int x10rt_init(int numArgs, String[] args);
    
    private static native int x10rt_finalize();

    private static native int x10rt_registration_complete();

    /*
     * Native method exported from x10rt_front.h that are related to Places
     */
    private static native int x10rt_nplaces();
        
    private static native int x10rt_ndead();
    
    private static native boolean x10rt_is_place_dead(int place);
    
    private static native int x10rt_here();
    
    /*
     * Subset of x10rt_front.h API related to messages that actually needs
     * to be exposed at the Java level (as opposed to being used
     * in the native code backing the native methods of MessageHandlers.
     */
    private static native int x10rt_probe();
    
    private static native int x10rt_blocking_probe();
}
