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

    static final boolean REPORT_UNCAUGHT_USER_EXCEPTIONS = true;

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
      System.loadLibrary(libName);

      // TODO: For now we are not trying to plumb the command line arguments from
      //       the program's main method into X10RT.  We really can't easily do this
      //       unless we change this code to be run via an explicit static method in
      //       X10RT instead of doing it in the class initializer.  
      //       Consider whether or not we should make this change....

      x10rt_init(0, null);

      initializeMessageHandlers();

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
    public static int here() {
      assert state.compareTo(State.BOOTED) >= 0;
      return here;
    }

    /**
     * Return the number of places in the computation.
     * @return the number of places in the computation.
     */
    public static int numPlaces() {
      assert state.compareTo(State.BOOTED) >= 0;
      return numPlaces;
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
    
    public static native void initializeMessageHandlers();
    
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
    
    public static native void sendJavaRemote(int place, int messageId, int arraylen, byte[] rawBytes);
    
    // Invoked from native code.
    private static void receiveJavaGeneral(int messageId, byte[] args) {
    	try{
    		System.out.println("@MultiVM : receiveJavaGeneral is called");
    		System.out.println("Message ID: "+ messageId);
    		java.io.ByteArrayInputStream byteStream 
    			= new java.io.ByteArrayInputStream(args);
    		System.out.println("receiveJavaGeneral: ByteArrayInputStream");
    		java.io.ObjectInputStream objStream = new java.io.ObjectInputStream(byteStream);
    		System.out.println("receiveJavaGeneral: ObjectInputStream");
    		x10.core.fun.VoidFun_0_0 actObj = (x10.core.fun.VoidFun_0_0) objStream.readObject();
    		System.out.println("receiveJavaGeneral: after cast");
    		actObj.apply();
    		System.out.println("receiveJavaGeneral: after apply");
    		objStream.close();
    		System.out.println("receiveJavaGeneral is done !");
    	} catch(Exception ex){
    		System.out.println("receiveGeneral error !!!");
    		ex.printStackTrace();
    	}
    }

}
