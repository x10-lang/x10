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
@NativeRep("java", "com.ibm.tuningfork.tracegen.types.EventAttribute", null, null)
public value EventAttribute {

	public native def this(name:String, description:String, type:ScalarType);
	
	@Native("java", "#0.getName()")
	public native def getName():String;

	@Native("java", "#0.getDescription()")
	public native def getDescription():String;

	@Native("java", "#0.getType()")
	public native def getType():ScalarType;
}
