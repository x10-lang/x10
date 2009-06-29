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
@NativeRep("java", "com.ibm.tuningfork.tracegen.IBookmarkEvent", null, null)
public value BookmarkEvent {
	
	@Native("java", "#0.addBookmark(#1,#2)")
	public native def addValue(feedlet:Feedlet, value:String):void;
}
