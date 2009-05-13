/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.util;

/** Random number generator. */
public class Random {
    public def this() {
        this(Timer.milliTime());
    }
    
    public def this(seed: Long) {
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
        
        var n: int = maxPlus1;

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
            var x: int = nextInt();
            for (var j: int = 0; j < 4; j++) {
                if (i > buf.length)
                    return;
                buf(i) = (x & 0xff) as Byte;
                i++;
                x >>= 8;
            }
        }
    }
     
    /** Return a 64-bit random (long) integer */
    public def nextLong(): long = ((nextInt() as Long) << 32) | nextInt() & 0xFFFFFFFFL;

    public def nextLong(maxPlus1: long): long {
        if (maxPlus1 <= 0)
            return 0L;
        
        var n: long = maxPlus1;

        if ((n & -n) == n) {
            // If a power of 2, just mask nextInt
            return nextLong() & (n-1);
        }

        // Get the next power of 2 greater than n
        var pow2: long = 1;
        while (pow2 < n)
            pow2 <<= 1;

        // Keep generating numbers of the right size until we get
        // one in range.  The expected number of iterations is 2.
        var x: long;

        do {
            x = nextLong() & (pow2-1);
        } while (x >= n);

        return x;
    }

    /** Return a random boolean. */
    public def nextBoolean(): boolean = nextInt() < 0;

    /** Return a random float. */
    public def nextFloat(): float = (nextInt() >>> (32-24)) / (1<<24 as Float);

    /** Return a random double. */
    public def nextDouble(): double = (nextLong() >>> (64-53)) / (1L<<53 as Double);

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

    private const N: int = 624;
    private const M: int = 397;

    private var index: int;
    private var MT: Rail[int] = Rail.makeVar[int](N);

    public def init(seed: long): Void {
        // Ensure the seed is nonzero.
        if (seed == 0L) {
            init(4357L);
            return;
        }

        // Set the initial buffer using a PRNG from
        // Knuth, vol 2, 2nd ed, p. 102
        MT(0) = (seed as Long) as Int;
        for (var i: int = 1; i < N; i++) {
            MT(i) = (69069L * MT(i-1)) as Int;
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
        var i: int = 0;
        var s: int;
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
