/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
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
	static private final AtomicInteger poolNumber = new AtomicInteger(0);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(0);
	private final String namePrefix;

	public X10ThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";
	}

	public Thread newThread(final Runnable workerRunnable) {
		final int x = threadNumber.getAndIncrement();
		String threadName = namePrefix + x;
		Runnable r = VMInterface.mapPoolThreadToCPU(workerRunnable, this.poolNumber.get(), x, threadName);
		Thread t = new PoolRunner(group, r, threadName);
		if (t.isDaemon()) t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
