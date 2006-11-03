/*
 * Created on Sep 28, 2004
 */
package x10.runtime;

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
    public static final boolean ASSIGN_WORKER_THREADS_TO_CPUS = Configuration.ASSIGN_WORKER_THREADS_TO_CPUS;

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
    static final Runnable mapPoolThreadToCPU(final Runnable workerRunnable, final int poolNumber, final int workerWithinPool) {
        if (ASSIGN_WORKER_THREADS_TO_CPUS && (numCPUs != 0)) {
            final int y = globalThreadNumber.getAndIncrement();
            return new Runnable() {
                    public void run() {
                        putMeOnCPU(y % numCPUs);
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
        } finally {
            numCPUs = n;
        }
    }
    
}

