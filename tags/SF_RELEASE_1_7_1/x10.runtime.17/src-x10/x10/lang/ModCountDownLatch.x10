/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.runtime.kernel.Lock;
import x10.runtime.kernel.NativeThread;

public class ModCountDownLatch {
	private var count: nat;
	private val lock = new Lock();
	private val stack = new Stack[NativeThread]();
	
	public def this(count: nat) {
		if (count < 0) throw new IllegalArgumentException("count < 0 in ModCountDownLatch");
		this.count = count;
	}
	
    public def updateCount(): void {
    	lock.lock();
    	count += 1;
    	lock.unlock();
    }

    public def countDown(): void {
    	lock.lock();
    	if (count > 0) { 
	    		--count;
	    		if (count == 0) {
    				for(th: NativeThread in stack) NativeThread.unpark(th);
    				// no need to clear the stack; the latch is now dead.
    			}
	}
    	lock.unlock();
    }

    public def await(): void {
    	lock.lock();
    	if (count > 0) {
    		stack.push(NativeThread.currentThread());
    		lock.unlock();
    		here.threadBlockedNotification();
    		while (count > 0) NativeThread.park();
    		here.threadUnblockedNotification();
    	} else {
    		lock.unlock();
	}
    }
	
    public def getCount(): nat {
	return count;
    }
}
