/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.NativeRep;

/**
 * Bridges XRX with Java runtime 
 */
@NativeRep("java", "x10.runtime.Clock") 
public interface ClockInterface {}
