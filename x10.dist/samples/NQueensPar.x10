/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 *  (C) Copyright Australian National University 2011.
 */

/**
 * Compute the number of solutions to the N queens problem.
 */
public class NQueensPar {
    public static val EXPECTED_SOLUTIONS =
        [0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712, 365596, 2279184, 14772512];

    val N:Int;
    val P:Int;
    var nSolutions:Int = 0;
    val R:Region(1){rect};

    def this(N:Int, P:Int) { 
       this.N=N;
       this.P=P;
       this.R = 0..(N-1);
    }

    def start() {
        new Board().parSearch();
    }

    class Board {
        val q: Rail[Int];
        /** The number of low-rank positions that are fixed in this board for the purposes of search. */
        var fixed:Int;
        def this() {
            q = new Rail[Int](N);
            fixed = 0;
        }

        def this(b:Board) {
            this.q = new Rail[Int](N);
            Array.copy(b.q, q);
            this.fixed = b.fixed;
        }

        /** 
         * @return true if it is safe to put a queen in file <code>j</code>
         * on the next rank after the last fixed position.
         */
        def safe(j:Int) {
            for (k in 0..(fixed-1)) {
                if (j == q(k) || Math.abs(fixed-k) == Math.abs(j-q(k)))
                    return false;
            }
            return true;
        }

        /** Search all positions for the current board. */
        def search() {
            for ([k] in R) searchOne(k);
        }

        /**
         * Modify the current board by adding a new queen
         * in file <code>k</code> on rank <code>fixed</code>,
         * and search for all safe positions with this prefix.
         */
        def searchOne(k:Int) {
            if (safe(k)) {
                if (fixed==(N-1)) {
                    // all ranks safely filled
                    atomic NQueensPar.this.nSolutions++;
                } else {
                    q(fixed++) = k;
                    search();
                    fixed--;
                }
            }
        }

        /**
         * Search this board, dividing the work between threads
         * using a block distribution of the current free rank.
         */
        def parSearch()  {
            val count = N/P;
            val extra = N%P;
            for (thread in 0..(P-1)) async {
                val board = new Board(this);
                val start = thread<=extra ? (thread*(count+1)) : (thread * count + extra);
                val end = start + (thread<extra ? (count+1) : count) - 1;
                for (k in start..end) {
                    board.searchOne(k);
                }
            }
        }
    }

    public static def main(args:Rail[String])  {
        val n = args.size > 0 ? Int.parse(args(0)) : 8;
        Console.OUT.println("N=" + n);
        //warmup
        //finish new NQueensPar(12, 1).start();
        val ps = [1,2,4];
        for (var i:Int = 0; i < ps.size; i++) {
            Console.OUT.println("starting " + ps(i) + " threads");
            val nq = new NQueensPar(n,ps(i));
            var start:Long = -System.nanoTime();
            finish nq.start();
            val result = nq.nSolutions==EXPECTED_SOLUTIONS(nq.N);
            start += System.nanoTime();
            start /= 1000000;
            Console.OUT.println("NQueensPar " + nq.N + "(P=" + ps(i) +
                    ") has " + nq.nSolutions + " solutions" +
                    (result? " (ok)." : " (wrong).") + "time=" + start + "ms");
        }
    }
}
