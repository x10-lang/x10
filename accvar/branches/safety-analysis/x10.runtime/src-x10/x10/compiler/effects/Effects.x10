/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.compiler;.effects

import x10.lang.annotations.StatementAnnotation;

/**
 * A statement is marked @Unsafe if is not safe.
 * @author vj
 */ 
public struct Effects {
	public def this() {}
	public incomplete def and(x:Effects):Effects;
	public incomplete def loc[T](x:T):ValRail[T];
    public incomplete def all[T](x:Array[T]):ValRail[T];
	public incomplete def read[T](x:ValRail[T]):Effects;
	public incomplete def write[T](x:ValRail[T]):Effects;
	public incomplete def touch[T](x:ValRail[T]):Effects;
	public incomplete def atomicInc[T](x:ValRail[T]):Effects ;
	public incomplete def atomicDec[T](x:ValRail[T]):Effects;
}
