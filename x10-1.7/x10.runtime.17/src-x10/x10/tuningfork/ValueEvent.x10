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
@NativeRep("java", "com.ibm.tuningfork.tracegen.IValueEvent", null, null)
public value ValueEvent {
	
	@Native("java", "#0.addValue(#1,#2)")
	public native def addValue(feedlet:Feedlet, value:double):void;
}
