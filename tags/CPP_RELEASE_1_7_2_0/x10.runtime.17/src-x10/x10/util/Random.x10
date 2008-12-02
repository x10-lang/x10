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

/** Random number generator. */
public class Random {
    var seed: long;
    
    public def this(): Random {
       this(Timer.milliTime());
    }
    
    public def this(seed: Long): Random {
        setSeed(seed);
    }
    
    public def setSeed(seed: Long): void {
        this.seed = seed;
    }
     
	public def nextInt(): Int = 17;

	public def nextInt(n: int): int = 0;
     
    public def nextLong(): long = 37L;
     
    public def nextBoolean(): boolean = false;
      
    public def nextFloat(): float = 3.14f;
     
    public def nextDouble(): double = 2.71;
}
