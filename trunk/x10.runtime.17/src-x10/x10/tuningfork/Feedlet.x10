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

	@Native("java", "#0.addEvent(#1,#2,#3)")
	public native def addEvent(eventType:EventType, v1:int, v2:int):void;

	@Native("java", "#0.addEvent(#1,#2,#3,#4)")
	public native def addEvent(eventType:EventType, v1:int, v2:int, v3:int):void;

	@Native("java", "#0.addEvent(#1,#2,#3,#4,#5)")
	public native def addEvent(eventType:EventType, v1:int, v2:int, v3:int, v4:int):void;

	@Native("java", "#0.addEvent(#1,#2)")
	public native def addEvent(eventType:EventType, value:long):void;

	@Native("java", "#0.addEvent(#1,#2,#3)")
	public native def addEvent(eventType:EventType, v1:long, v2:long):void;

	@Native("java", "#0.addEvent(#1,#2)")
	public native def addEvent(eventType:EventType, value:double):void;

	@Native("java", "#0.addEvent(#1,#2,#3)")
	public native def addEvent(eventType:EventType, v1:double, v2:double):void;

	@Native("java", "#0.addEvent(#1,#2)")
	public native def addEvent(eventType:EventType, value:String):void;

	@Native("java", "#0.addEvent(#1,#2,#3)")
	public native def addEvent(eventType:EventType, v1:int, v2:long):void;

	@Native("java", "#0.addEvent(#1,#2,#3)")
	public native def addEvent(eventType:EventType, v1:int, v2:double):void;

	@Native("java", "#0.addEvent(#1,#2,#3)")
	public native def addEvent(eventType:EventType, v1:int, v2:String):void;

	@Native("java", "#0.addEvent(#1,#2,#3)")
	public native def addEvent(eventType:EventType, v1:long, v2:double):void;

	@Native("java", "#0.addEvent(#1, (int[])((#2).value), (long[])((#3).value), (double[])((#4).value), (String[])((#5).value))")
	public native def addEvent(eventType:EventType, ints:Rail[int], longs:Rail[long], doubles:Rail[double], strings:Rail[String]):void;
}
