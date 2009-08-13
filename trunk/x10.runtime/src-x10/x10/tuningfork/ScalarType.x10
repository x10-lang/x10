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
 * A scalar type (e.g. the type of a field of an event).
 */
@NativeRep("java", "com.ibm.tuningfork.tracegen.types.ScalarType", null, null)
@NativeRep("c++", "x10aux::ref<x10::tuningfork::ScalarType>", "x10::tuningfork::ScalarType", null)
public value ScalarType {
	
	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.INT")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(INT)")
	public const INT = makeIntType();
	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.INT")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(INT)")
	private native static def makeIntType():ScalarType;

	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.LONG")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(LONG)")
	public const LONG = makeLongType();
	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.LONG")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(LONG)")
	private native static def makeLongType():ScalarType;

	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.DOUBLE")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(DOUBLE)")
	public const DOUBLE = makeDoubleType();
	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.DOUBLE")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(DOUBLE)")
	private native static def makeDoubleType():ScalarType;

	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.STRING")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(STRING)")
	public const STRING = makeStringType();
	@Native("java", "com.ibm.tuningfork.tracegen.types.ScalarType.STRING")
	@Native("c++", "x10::tuningfork::ScalarType::FMGL(STRING)")
	private native static def makeStringType():ScalarType;

	private native def this(name:String, description:String);
	
	@Native("java", "#0.getName()")
	@Native("c++", "#0.getName()")
	public native def getName():String;

	@Native("java", "#0.getDescription()")
	@Native("c++", "#0.getDescription()")
	public native def getDescription():String;
}
