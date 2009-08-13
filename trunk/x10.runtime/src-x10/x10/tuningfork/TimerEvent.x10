/*
 *
 * (C) Copyright IBM Corporation 2009
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.tuningfork;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * 
 */
@NativeRep("java", "com.ibm.tuningfork.tracegen.ITimerEvent", null, null)
public value TimerEvent {
	
	@Native("java", "#0.start(#1)")
	public native def start(feedlet:Feedlet):void;

	@Native("java", "#0.stop(#1)")
	public native def stop(feedlet:Feedlet):void;
}
