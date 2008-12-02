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

/** Random number generator, following the algorithm of java.util.Random. */
public class Random {
    var seed: long;
    
    public def this(): Random {
       this(Timer.milliTime());
    }
    
    public def this(seed: Long): Random {
        setSeed(seed);
    }
    
    public def setSeed(seed: Long): void {
        this.seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
    }
     
    private def nextBits(bits: int): int {
        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        return (seed >>> (48 - bits)) to Int;
    }
	
	public def nextInt() = nextBits(32);
	
	public def nextInt(n: int): int {
	    assert n > 0;

        if ((n & -n) == n)  // i.e., n is a power of 2
            return ((n * (nextBits(31) to long)) >> 31) to Int;

        var bits: int;
        var v: int;
        
        do {
            bits = nextBits(31);
            v = bits % n;
        } while (bits - v + (n-1) < 0);
 
        return v;
    }
     
    public def nextLong(): long { 
        return ((nextBits(32) to long) << 32) + nextBits(32);
    }
     
    public def nextBoolean() = nextBits(1) != 0;
      
    public def nextFloat(): float {
        return nextBits(24) / ((1 << 24) to float);
    }
     
    public def nextDouble(): double {
       return (((nextBits(26) to long) << 27) + nextBits(27)) / ((1L << 53) to double);
    }
}
