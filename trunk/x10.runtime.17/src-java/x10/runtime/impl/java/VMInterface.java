/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
/*
 * Created on Sep 28, 2004
 */
package x10.runtime.impl.java;

import java.util.concurrent.atomic.*;

/**
 * This class introduces static final fields that are initialized with values provided by other classes.
 * Any references to these fields should be recognized as constants by a JIT compiler compiling code that 
 * references any of these fields.  (It is important to ensure that classes that reference these fields
 * do not introduce a cycle in class loading dependences.)
 * 
 * @author Vivek Sarkar (based on ideas suggested by Allan Kielstra, Igor Peshansky, Chris Donawa, Raj Barik)
 */
public final class VMInterface {

    public static final int NUMBER_OF_LOCAL_PLACES = Configuration.NUMBER_OF_LOCAL_PLACES;
    
    public static final boolean ABSTRACT_EXECUTION_STATS = Configuration.ABSTRACT_EXECUTION_STATS;
    
    public static final boolean ABSTRACT_EXECUTION_TIMES = Configuration.ABSTRACT_EXECUTION_TIMES;

    // Set to true if individual PoolRunners should be pre-assigned to
    // CPUs on an SMP system.
    public static final boolean BIND_THREADS = Configuration.BIND_THREADS;
    public static final boolean BIND_THREADS_DIAGNOSTICS = Configuration.BIND_THREADS_DIAGNOSTICS;

    // The following methods are all specially treated by the Testarossa
    // JIT and may be specially treated by any JIT.
    // call this method early so that Helpers will be loaded before
    // we attempt to JIT compile any calls to methods below
    public static final void loadMeInMain() { }

    // The Testarossa JIT (in J9) will use this to allow
    // speculative loads of array elements beyond the end of
    // the array without bounds check violations
    public static final int speculateIndex(int index,int upperBound) {
        if(index >=upperBound) return 0;
        return index;
    }
    // The Testarossa JIT (in J9) will turn this into a direct
    // call to the appropriate system routine on AIX
    public static final int getCPU() { return -1; }

    // When the Testarossa JIT (in J9) sees something like:
    //    arr[x10JITHelpers.Helpers.noBoundsCheck(x)]
    // it will treat it as:
    //    arr[(x)] only with no bounds check
    public static final int noBoundsCheck(int i) { return i; }

    // When the JIT sees something like:
    //    checkLowBound(i,low)
    // it can inline the following method body, and treat this method as an intrinsic that is a candidate for redundancy optimizations
    public static int checkLowBound(int i, int low)  { if (i < low) throw new ArrayIndexOutOfBoundsException(); return i; }

    // When the JIT sees something like:
    //    checkHighBound(i,high)
    // it can inline the following method body, and treat this method as an intrinsic that is a candidate for redundancy optimizations
    public static int checkHighBound(int i, int high) { if (i > high) throw new ArrayIndexOutOfBoundsException(); return i; }

    // NOTE: we can later evaluate if we want to also create an optimized combined call for low and high bounds, as Igor suggested

    // When the Testarossa JIT (in J9) sees something like:
    //    noNullCheck(x)
    // it will treat it as:
    //    (x) only with no null reference check
    public static final Object noNullCheck(Object o) { return o; }
   
    // When the Testarossa JIT (in J9) sees something like:
    //    noCastCheck(x)
    // it will treat it as:
    //    (x) only with no type cast check
    public static final Object noCastCheck(Object o) { return o; }

    // Given a j.u.c Worker Runnable, construct a Runnable that will first
    // ensure that the Worker thread runs on "the right" CPU
    static final Runnable mapPoolThreadToCPU(final Runnable workerRunnable, final int placeNumber, final int workerWithinPool, String threadName) {
        if (BIND_THREADS && (numCPUs != 0)) {
			final int CPUsPerPlace = numCPUs / Configuration.NUMBER_OF_LOCAL_PLACES;
			final int firstCPUInThisPlace = CPUsPerPlace * placeNumber;
			final int numCPUsInThisPlace = (placeNumber == Configuration.NUMBER_OF_LOCAL_PLACES - 1) ? (numCPUs - firstCPUInThisPlace) : CPUsPerPlace;
			final int myCPU = (firstCPUInThisPlace + workerWithinPool % numCPUsInThisPlace) % numCPUs ;
			if ( BIND_THREADS_DIAGNOSTICS ) System.err.println("BIND_THREADS: Mapping thread " + threadName + " to CPU " + myCPU);
			return new Runnable() {
				public void run() {
					putMeOnCPU(myCPU);
					workerRunnable.run();
				}
			};
		} else {
            return workerRunnable;
        }
    }
      
    private final static AtomicInteger globalThreadNumber = new AtomicInteger(0);
    private final native static int getNumCPUs();
    private final static int numCPUs;
    private final static String thrdSuppLib = "X10ThreadSupp";
    private final native static void putMeOnCPU(int i);
    static {
        int n = 0;
        try {
            System.loadLibrary(thrdSuppLib);
            n = getNumCPUs();
        } catch (java.lang.UnsatisfiedLinkError ule) {
        	if ( BIND_THREADS_DIAGNOSTICS ) System.err.println("BIND_THREADS: Library " + thrdSuppLib + " not found???");
        } finally {
            numCPUs = n;
        }
    }
    
}

