/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.util;

import x10.util.concurrent.AtomicLong;
import x10.compiler.Inline;

/** 
 * Generate pseudo-random numbers.
 *
 * The underlying pseudo-random stream is generated 
 * using the SplittableRandom algorithm described by Steele, Lea, and Flood
 * in "Fast Splittable Pseudorandom Number Generators", OOPSLA 2014.
 */
public final class Random {
    private static val defaultGen = new AtomicLong(System.nanoTime());
    private static val GOLDEN_GAMMA = 0x9e3779b97f4a7c15;
    private static val FLOAT_ULP = 1.0f / (1 << 24);
    private static val DOUBLE_ULP = 1.0 / (1 << 53);

    private var seed:Long;
    private val gamma:Long;

    private def this(seed:Long, gamma:Long) {
        this.seed = seed;
        this.gamma = gamma;
    }

    public def this(seed:Long) {
        this(seed, GOLDEN_GAMMA);
    }

    public def this() {
        val s = defaultGen.getAndAdd(2 * GOLDEN_GAMMA);
        seed = mix64(s);
        gamma = mixGamma(s + GOLDEN_GAMMA);
    }

    /** Split and return a new Random instance derived from this one */
    public def split() = new Random(mix64(nextSeed()), mixGamma(nextSeed()));
    
     
    /** Return a 32-bit random integer */
    public @Inline def nextInt():Int = mix32(nextSeed());

    /** Return a 32-bit random integer in the range 0 to maxPlus1-1
     * when maxPlus1 > 0. Return 0 if maxPlus1 <= 0 instead of throwing 
     * an IllegalArgumentException, to simplify user code.
     */
    public def nextInt(maxPlus1:Int):Int {
        if (maxPlus1 <= 0n)
            return 0n;
        
        var n:Int = maxPlus1;

        if ((n & -n) == n) {
            // If a power of 2, just mask nextInt
            return nextInt() & (n-1n);
        }

        var mask:Int = 1n;
        while ((n & ~mask) != 0n) {
            mask <<= 1n;
            mask |= 1n;
        }

        // Keep generating numbers of the right size until we get
        // one in range.  The expected number of iterations is 2.
        var x:Int;

        do {
            x = nextInt() & mask;
        } while (x >= n);

        return x;
    }

    public def nextBytes(buf:Rail[Byte]):void {
        var i:Long = 0;
        while (true) {
            var x:Long = nextLong();
            for (1..8) {
                if (i >= buf.size)
                    return;
                buf(i) = (x & 0xff) as Byte;
                i++;
                x >>= 8;
            }
        }
    }
     
    /** Return a 64-bit random (Long) integer */
    public @Inline def nextLong():Long = mix64(nextSeed());

    public def nextLong(maxPlus1:Long):Long {
        if (maxPlus1 <= 0n)
            return 0;
        
        var n:Long = maxPlus1;

        if ((n & -n) == n) {
            // If a power of 2, just mask nextInt
            return nextLong() & (n-1);
        }

        var mask:Long = 1;
        while ((n & ~mask) != 0) {
            mask <<= 1n;
            mask |= 1;
        }

        // Keep generating numbers of the right size until we get
        // one in range.  The expected number of iterations is 2.
        var x:Long;

        do {
            x = nextLong() & mask;
        } while (x >= n);

        return x;
    }

    /** Return a random boolean. */
    public @Inline def nextBoolean():Boolean = nextInt() < 0n;

    /** Return a random float between 0.0f and 1.0f. */
    public @Inline def nextFloat():Float = (nextInt() >>> 8) * FLOAT_ULP;

    /** Return a random double between 0.0 and 1.0. */
    public @Inline def nextDouble():Double = (nextLong() >>> 11) * DOUBLE_ULP;

    private @Inline def nextSeed() {
        return (seed += gamma);
    }

    private static @Inline def mix64(var z:long) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccd;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53;
        return z ^ (z >>> 33);
    }

    private static @Inline def mix32(var z:long) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccd;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53;
        return (z >>> 32) as Int; 
    }

    private static def mix64variant13(var z:long) {
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111eb;
        return z ^ (z >>> 31); 
    }

    private static def mixGamma(var z:long) {
        z = mix64variant13(z) | 1;
        val n = (z ^ (z >>> 1)).bitCount();
        if (n >= 24) { 
            z ^= 0xaaaaaaaaaaaaaaaa;
        }
        return z; 
    }
}
