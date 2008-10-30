/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Language.
 *
 */
package x10.runtime;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * X10 thread factory for JCU implementation.
 * @author Raj Barik, Vivek Sarkar -- 3/6/2006
 * @tardieu
 */
public class X10ThreadFactory implements ThreadFactory {
	private final int placeId;
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(0);
	private final String namePrefix;

	public X10ThreadFactory(int placeId) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null)? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "pool-" + placeId + "-thread-";
		this.placeId = placeId; 
	}

	public Thread newThread(final Runnable workerRunnable) {
		final int x = threadNumber.getAndIncrement();
		String threadName = namePrefix + x;
		Runnable r = VMInterface.mapPoolThreadToCPU(workerRunnable, placeId, x, threadName);
		Thread t = new X10Thread(group, r, threadName, placeId);
		if (t.isDaemon()) t.setDaemon(false);
		if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
