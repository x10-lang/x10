/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.util.Random") 
public class Random {

	@Native("java", "new java.util.Random()")
    public native def this(): Random;

	@Native("java", "new java.util.Random(#1)")
    public native def this(n: long): Random;
    
	@Native("java", "#0.nextInt(#1)")
    public native def nextInt(n: int): int;
}
