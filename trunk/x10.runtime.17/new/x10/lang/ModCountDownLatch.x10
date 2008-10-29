/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

public class ModCountDownLatch {
	private var count: nat;
	
	public def this(init: nat) {
		count = init;
	}
	
    public atomic def updateCount(): void {
    	count++;
    }

    public atomic def countDown(): void {
    	if (count > 0) count--;
    }

    public def await(): void {
    	if (count > 0) await count == 0;
	}
	
	public def getCount(): nat {
		return count;
	}
}
