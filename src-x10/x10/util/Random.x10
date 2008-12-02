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
    public def this(): Random {
        this(Timer.milliTime());
    }
    
    public def this(seed: Long): Random {
        setSeed(seed);
    }
    
    public def setSeed(seed: Long): void {
        init(seed);
    }
     
    /** Return a 32-bit random integer */
    public def nextInt(): Int = random();

    /** Return a 32-bit random integer in the range 0 to n-1.
     * If n <= 0, returns 0. */
    public def nextInt(maxPlus1: int): int {
        if (maxPlus1 <= 0)
            return 0;
        
        var n = maxPlus1;

        if ((n & -n) == n) {
            // If a power of 2, just mask nextInt
            return nextInt() & (n-1);
        }

        // Get the next power of 2 greater than n
        var pow2: int = 1;
        while (pow2 < n)
            pow2 <<= 1;

        // Keep generating numbers of the right size until we get
        // one in range.  The expected number of iterations is 2.
        var x: int;

        do {
            x = nextInt() & (pow2-1);
        } while (x >= n);

        return x;
    }

    public def nextBytes(buf: Rail[Byte]): Void {
        var i: int = 0;
        while (true) {
            val x = nextInt();
            for (var j = 0; j < 4; j++) {
                if (i > buf.length)
                    return;
                buf(i) = (x & 0xff) to Byte;
                i++;
                x >>= 8;
            }
        }
    }
     
    /** Return a 64-bit random (long) integer */
    public def nextLong(): long = (nextInt() << 32) | nextInt();

    public def nextLong(maxPlus1: long): long {
        if (maxPlus1 <= 0)
            return 0;
        
        var n = maxPlus1;

        if ((n & -n) == n) {
            // If a power of 2, just mask nextInt
            return nextLong() & (n-1);
        }

        // Get the next power of 2 greater than n
        var pow2: int = 1;
        while (pow2 < n)
            pow2 <<= 1;

        // Keep generating numbers of the right size until we get
        // one in range.  The expected number of iterations is 2.
        var x: int;

        do {
            x = nextLong() & (pow2-1);
        } while (x >= n);

        return x;
    }

    /** Return a random boolean. */
    public def nextBoolean(): boolean = (nextInt() & 1) != 0;

    /** Return a random float. */
    public def nextFloat(): float = Float.fromIntBits(nextInt());

    /** Return a random double. */
    public def nextDouble(): double = Double.fromLongBits(nextLong());

/**
 * Mersenne twister.
 *
 * Based on the public domain implementation by Michael Brundage at:
 *
 * http://www.qbrundage.com/michaelb/pubs/essays/random_number_generation.html
 *
 * and the implementation described in the original paper:
 *
 * M. Matsumoto and T. Nishimura, "Mersenne Twister: A 623-dimensionally
 * equidistributed uniform pseudorandom number generator", ACM Trans. on
 * Modeling and Computer Simulation, 8(1), January, pp. 3--30 (1998)
 */

    private const int N = 624;
    private const int M = 397;

    private var index: int;
    private var MT: Rail[int] = Rail.makeVar[int](N);

    public def init(seed: long): Void {
        // Ensure the seed is nonzero.
        if (seed == 0L)
            seed = 4357L;

        // Set the initial buffer using a PRNG from
        // Knuth, vol 2, 2nd ed, p. 102
        MT(0) = seed to Int;
        for (var i: int = 1; i < N; i++) {
            MT(i) = (69069L * MT(i-1)) to Int;
        }

        // make sure we twist once.
        index = 0;
        twist();
    }

    public def random(): int {
        if (index == N) {
            index = 0;
            twist();
        }
        return MT(index++);
    }

    private def twist(): void {
        int i = 0;
        int s;
        for (; i < N - M; i++) {
            s = (MT(i) & 0x80000000) | (MT(i+1) & 0x7FFFFFFF);
            MT(i) = MT(i+M) ^ (s >> 1) ^ ((s & 1) * 0x9908B0DF);
        }
        for (; i < N-1; i++) {
            s = (MT(i) & 0x80000000) | (MT(i+1) & 0x7FFFFFFF);
            MT(i) = MT(i-(N-M)) ^ (s >> 1) ^ ((s & 1) * 0x9908B0DF);
        }
    
        s = (MT(N-1) & 0x80000000) | (MT(0) & 0x7FFFFFFF);
        MT(N-1) = MT(M-1) ^ (s >> 1) ^ ((s & 1) * 0x9908B0DF);
    }
}
