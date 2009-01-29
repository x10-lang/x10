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
 * A Feedlet is used to add events to the trace. It typically 
 * represents a logical thread of control or a resource. 
 */
@NativeRep("java", "com.ibm.tuningfork.tracegen.IFeedlet", null, null)
public class Feedlet {
	
	@Native("java", "#0.addEvent(#1)")
	public native def addEvent(eventType:EventType):void;

	@Native("java", "#0.addEvent(#1,#2)")
	public native def addEvent(eventType:EventType, value:int):void;

	@Native("java", "#0.addEvent(#1,#2)")
	public native def addEvent(eventType:EventType, value:long):void;

	@Native("java", "#0.addEvent(#1,#2)")
	public native def addEvent(eventType:EventType, value:double):void;

	@Native("java", "#0.addEvent(#1,#2)")
	public native def addEvent(eventType:EventType, value:String):void;
}
