/*
 *
 * (C) Copyright IBM Corporation 2007
 *
 *  This file is part of X10 runtime. It is 
 *  governed by the licence under which 
 *  X10 is released.
 *
 */
package x10.runtime.xws;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * A Frame holds the PC and dirty live variables in a procedure call
 * that contains an async spawn. 
 * API note: Code written by users of the work-stealing API will typically
 * extend this class for every procedure containing async spawns and finishes.
 * 
 * Currently just an x10 facade using NativeRep around
 * the Java implementation.  Eventually will mostly
 * migrate to being in x10.
 */
@NativeRep("java", "x10.runtime.xws.impl.Frame", null, null)
public class Frame {
  protected native def this():Frame;
}
