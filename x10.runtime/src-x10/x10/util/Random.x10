/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.util;
import x10.compiler.NonEscaping;

/** 
 * Generate pseudo-random numbers.
 *
 * The underlying pseudo-random bit stream is generated 
 * using the Mersenne Twister as described in the paper:
 *   M. Matsumoto and T. Nishimura, "Mersenne Twister: A 623-dimensionally
 *   equidistributed uniform pseudorandom number generator", ACM Trans. on
 *   Modeling and Computer Simulation, 8(1), January, pp. 3--30 (1998)
 */
public class Random {
    private static val N = 624;
    private static val M = 397;

    private var index:Long;
    private val mt:Rail[Int]{self!=null,self.size==N};

    public def this() {
        this(System.nanoTime());
    }

    public def this(seed:Long) {
        mt = Unsafe.allocRailUninitialized[Int](N); // No need to zero; initialized by init
        init(seed);
    }
    
    public final def setSeed(seed:Long):void {
        init(seed);
    }
     
    /** Return a 32-bit random integer */
    public def nextInt():Int = random();

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
        var i:Int = 0n;
        while (true) {
            var x:Int = nextInt();
            for (var j:Int = 0n; j < 4n; j++) {
                if (i >= buf.size)
                    return;
                buf(i) = (x & 0xff) as Byte;
                i++;
                x >>= 8n;
            }
        }
    }
     
    /** Return a 64-bit random (Long) integer */
    public def nextLong():Long = ((nextInt() as Long) << 32n) | (nextInt() & 0xFFFFFFFFL);

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
    public def nextBoolean():Boolean = nextInt() < 0n;

    /** Return a random float between 0.0f and 1.0f. */
    public def nextFloat():Float = (nextInt() >>> (32n-24n)) / ((1<<24n) as Float);

    /** Return a random double between 0.0 and 1.0. */
    public def nextDouble():Double = (nextLong() >>> (64n-53n)) / ((1<<53n) as Double);

/*
 * Mersenne twister implementation.
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

    @NonEscaping public final def init(seed:Long):void {
	// If provided seed was 0, use 4357 instead.
        mt(0) = seed == 0 ? 4357n : (seed as Int);

        // Set the initial mt buffer using a PRNG from
        // Knuth, vol 2, 2nd ed, p. 102
        for (i in 1..(N-1)) {
            mt(i) = 69069n * mt(i-1) + 1n;
        }

        // make sure we twist once.
        index = 0;
        twist();
    }

    public def random():Int {
        if (index == N) {
            index = 0;
            twist();
        }
        var y:Int = mt(index++);
        y ^= (y >>> 11n);
        y ^= (y <<  7n) & 0x9D2C5680n;
        y ^= (y << 15n) & 0xEFC60000n;
        y ^= (y >>> 18n);
        return y;
    }

    private def twist():void {
        var i:Long = 0;
        var s:Int;
        for (; i < N - M; i++) {
            s = (mt(i) & 0x80000000n) | (mt(i+1) & 0x7FFFFFFFn);
            mt(i) = mt(i+M) ^ (s >>> 1n) ^ ((s & 1n) * 0x9908B0DFn);
        }
        for (; i < N-1; i++) {
            s = (mt(i) & 0x80000000n) | (mt(i+1) & 0x7FFFFFFFn);
            mt(i) = mt(i-(N-M)) ^ (s >>> 1n) ^ ((s & 1n) * 0x9908B0DFn);
        }
    
        s = (mt(N-1) & 0x80000000n) | (mt(0) & 0x7FFFFFFFn);
        mt(N-1) = mt(M-1) ^ (s >>> 1n) ^ ((s & 1n) * 0x9908B0DFn);
    }
}
