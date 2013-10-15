/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.util;
import x10.compiler.NonEscaping;

/** Random number generator. */
public class Random {
    public def this() {
        this(Timer.milliTime());
    }
    
    public def this(seed: Long) {
        setSeed(seed);
    }
    
    @NonEscaping public final def setSeed(seed: Long): void {
        init(seed);
    }
     
    /** Return a 32-bit random integer */
    public def nextInt(): Int = random();

    /** Return a 32-bit random integer in the range 0 to maxPlus1-1
     * when maxPlus1 > 0. Return 0 if maxPlus1 <= 0 instead of throwing 
     * an IllegalArgumentException, to simplify user code.
     */
    public def nextInt(maxPlus1: int): int {
        if (maxPlus1 <= 0n)
            return 0n;
        
        var n: int = maxPlus1;

        if ((n & -n) == n) {
            // If a power of 2, just mask nextInt
            return nextInt() & (n-1n);
        }

        var mask: int = 1n;
        while ((n & ~mask) != 0n) {
            mask <<= 1n;
            mask |= 1n;
        }

        // Keep generating numbers of the right size until we get
        // one in range.  The expected number of iterations is 2.
        var x: int;

        do {
            x = nextInt() & mask;
        } while (x >= n);

        return x;
    }

    public def nextBytes(buf: Rail[Byte]): void {
        var i: int = 0n;
        while (true) {
            var x: int = nextInt();
            for (var j: int = 0n; j < 4n; j++) {
                if (i >= buf.size)
                    return;
                buf(i) = (x & 0xff) as Byte;
                i++;
                x >>= 8n;
            }
        }
    }
     
    /** Return a 64-bit random (long) integer */
    public def nextLong(): long = ((nextInt() as Long) << 32n) | (nextInt() & 0xFFFFFFFFL);

    public def nextLong(maxPlus1: long): long {
        if (maxPlus1 <= 0n)
            return 0L;
        
        var n: long = maxPlus1;

        if ((n & -n) == n) {
            // If a power of 2, just mask nextInt
            return nextLong() & (n-1);
        }

        var mask: long = 1L;
        while ((n & ~mask) != 0L) {
            mask <<= 1n;
            mask |= 1L;
        }

        // Keep generating numbers of the right size until we get
        // one in range.  The expected number of iterations is 2.
        var x: long;

        do {
            x = nextLong() & mask;
        } while (x >= n);

        return x;
    }

    /** Return a random boolean. */
    public def nextBoolean(): boolean = nextInt() < 0n;

    /** Return a random float between 0.0f and 1.0f. */
    public def nextFloat(): float = (nextInt() >>> (32n-24n)) / ((1<<24n) as Float);

    /** Return a random double between 0.0 and 1.0. */
    public def nextDouble(): double = (nextLong() >>> (64n-53n)) / ((1L<<53n) as Double);

/*
 * Mersenne twister.
 *
 * Based on the public domain implementation by Michael Brundage at:
 *
 * http://www.qbrundage.com/michaelb/pubs/essays/random_number_generation.html
 * (Note: this implementation does not include tempering, which is critical if
 * initializing the buffer with a LCG.)
 *
 * and the implementation described in the original paper:
 *
 * M. Matsumoto and T. Nishimura, "Mersenne Twister: A 623-dimensionally
 * equidistributed uniform pseudorandom number generator", ACM Trans. on
 * Modeling and Computer Simulation, 8(1), January, pp. 3--30 (1998)
 */

    private static N: int = 624n;
    private static M: int = 397n;

    private var index: int;
    private var MT: Rail[int];

    @NonEscaping public final def init(seed: long): void {
        val mt = new Rail[int](N);
        MT=mt;
        // Ensure the seed is nonzero.
        if (seed == 0L) {
            init(4357L);
            return;
        }

        // Set the initial buffer using a PRNG from
        // Knuth, vol 2, 2nd ed, p. 102
        mt(0n) = (seed as Long) as Int;
        for (var i: int = 1n; i < N; i++) {
            mt(i) = (69069L * mt(i-1n) + 1n) as Int;
        }

        // make sure we twist once.
        index = 0n;
        twist(mt);
    }

    public def random(): int {
        if (index == N) {
            index = 0n;
            twist(MT);
        }
        var y:Int = MT(index++);
        y ^= (y >>> 11n);
        y ^= (y <<  7n) & 0x9D2C5680n;
        y ^= (y << 15n) & 0xEFC60000n;
        y ^= (y >>> 18n);
        return y;
    }

    private static def twist(MT:Rail[int]): void {
        var i: int = 0n;
        var s: int;
        for (; i < N - M; i++) {
            s = (MT(i) & 0x80000000n) | (MT(i+1n) & 0x7FFFFFFFn);
            MT(i) = MT(i+M) ^ (s >>> 1n) ^ ((s & 1n) * 0x9908B0DFn);
        }
        for (; i < N-1; i++) {
            s = (MT(i) & 0x80000000n) | (MT(i+1n) & 0x7FFFFFFFn);
            MT(i) = MT(i-(N-M)) ^ (s >>> 1n) ^ ((s & 1n) * 0x9908B0DFn);
        }
    
        s = (MT(N-1n) & 0x80000000n) | (MT(0n) & 0x7FFFFFFFn);
        MT(N-1n) = MT(M-1n) ^ (s >>> 1n) ^ ((s & 1n) * 0x9908B0DFn);
    }
}
