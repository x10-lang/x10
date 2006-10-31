package x10.runtime;

import java.util.concurrent.*;
import java.util.*;
import java.lang.*;
import java.util.concurrent.atomic.*;

/**
 * X10 thread factory for JCU implementation.
 * @author Raj Barik, Vivek Sarkar -- 3/6/2006
 */
public class X10ThreadFactory implements ThreadFactory {
	static final AtomicInteger poolNumber = new AtomicInteger(0);
	final ThreadGroup group;
	final AtomicInteger threadNumber = new AtomicInteger(0);
        final AtomicInteger gthreadNumber = new AtomicInteger(0);
	final String namePrefix;

	public X10ThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null)? s.getThreadGroup() :
							 Thread.currentThread().getThreadGroup();
		namePrefix = "pool-" +
					 poolNumber.getAndIncrement() +
					 "-thread-";
	}

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
	public Thread newThread(final Runnable r) {
                final int x = threadNumber.getAndIncrement();
                final int y = threadNumber.getAndIncrement();
                Runnable rr = r;
                if (numCPUs != 0) {
                    rr = new Runnable() {
                            public void run() {
                                putMeOnCPU(y % numCPUs);
                                r.run();
                            }
                        };
                }
		Thread t = new PoolRunner(group, rr, namePrefix + x);
		if (t.isDaemon())
			t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
