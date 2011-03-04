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
 * A Logger is responsible for generating a TuningFork trace (feed).
 * Traces are composed of events, organized into feedlets. 
 * The Logger object is used to create meta-data like EventTypes
 * and Properties and to create Feedlet objects.  The Feedlet objects
 * are then used to actually generate events into the Feed.
 */
@NativeRep("java", "com.ibm.tuningfork.tracegen.ILogger", null, null)
public class Logger {

	@Native("java", "#0.makeTimerEvent(#1)")
	public native def makeTimerEvent(name:String):TimerEvent;

	@Native("java", "#0.makeBookmarkEvent(#1)")
	public native def makeBookmarkEvent(name:String):BookmarkEvent;

	@Native("java", "#0.makeValueEvent(#1)")
	public native def makeValueEvent(name:String):ValueEvent;

	@Native("java", "#0.addEventType(#1)")
	public native def addEventType(et:EventType):void;
	
	@Native("java", "#0.addProperty(#1, #2)")
	public native def addProperty(propertyName:String, value:String):void;

	@Native("java", "#0.makeFeedlet(#1,#2)")
	public native def makeFeedlet(name:String, description:String):Feedlet;

	
}
