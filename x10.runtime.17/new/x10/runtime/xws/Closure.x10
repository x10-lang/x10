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
 * A closure corresponds to a slow invocation of a task. This 
 * class is intended to be subclassed by client code. Subclasses
 * may specify extra state (e.g. fields such as result),
 * and will supply implementations of compute and executeAsInlet. 
 * The code for a slow invocation of a task
 * should live in compute. The code for executeAsInlet should
 * specify where the result of the closure is to be deposited
 * in the parent closure.
 * 
 * Currently just an x10 facade using NativeRep around
 * the Java implementation.  Eventually will mostly
 * migrate to being in x10.
 */
@NativeRep("java", "x10.runtime.xws.impl.Closure")
public class Closure /* implements Executable */ {
  // public Frame frame;
  // public int joinCount;
}
