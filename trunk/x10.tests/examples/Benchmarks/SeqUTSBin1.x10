// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * From the paper:
 *
 * The unbalanced tree search (UTS) problem is to count the number of
 * nodes in an implicitly constructed tree that is parameterized in
 * shape, depth, size, and imbalance.
 *
 * A node in a binomial tree has m children with probability q and has
 * no children with probability 1 - q, where m and q are parameters of
 * the class of binomial 3 trees. When qm < 1, this process generates
 * a finite tree with expected size 1/(1-qm).  Since all nodes follow
 * the same distribution, the trees generated are self-similar and the
 * distribution of tree sizes and depths follow a power law [3]. The
 * variation of subtree sizes increases dramatically as qm approaches
 * The root-specific branching factor b0 can be set sufficiently high
 * to generate an interesting variety of subtree sizes below the root
 * according to the power law.  Alternatively, b0 can be set to 1, and
 * a specific value of r chosen to generate a tree of a desired size
 * and imbalance.
 * 
 * @author bdlucas
 */

import x10.util.Random;

class SeqUTSBin1 extends Benchmark {

    //
    // parameters
    //

    const r0 = 0;                      // seed for root

    //const b0 = 1000000;
    //const q = 0.2;
    //const m = 4;
    //def expected() = 4993764.0;

    const b0 = 50000;                  // branching factor of root node
    const q = 0.12;                    // prob of non-zero branching factor
    const m = 8;                       // branching factor is m with prob q
    def expected() = 1234872.0;        // expected size given above params 

    def operations() = size to double; // work is proportional to size


    //
    // the benchmark
    //
    // For now use util.Random instead of SHA. To substitute SHA
    // redefine descriptor, next(), and number().
    //
    // Instead of actually using util.Random, we replicate its
    // function here to avoid allocating a Random object.
    //

    static type descriptor = long;

    def next(r:descriptor, i:nat) {
        var seed: long = r+i;
        seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        for (var k:int=0; k<11; k++)
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        val l0 = (seed >>> (48 - 32)) to int;
        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        val l1 = (seed >>> (48 - 32)) to int;
        return ((l0 to long) << 32) + l1;
    }

    const scale = (long.MAX_VALUE to double) - (long.MIN_VALUE to double);
    def number(r:descriptor) = (r / scale) - (long.MIN_VALUE / scale);


    //
    // node visitor
    //

    var size:int = 0;
    var sumb:int = 0;

    def visit(r:long) {
        val b = number(r)<q? m : 0; // binomial distribution
        //System.out.println(" " + number(r));
        sumb += b;
        size++;
        for (var i:int=0; i<b; i++)
            visit(next(r,i));
    }
    
    //
    // do a tree
    //

    var first:boolean = true;

    def once() {

        // root node
        size = 0;
        sumb = 0;
        for (var i:int=0; i<b0; i++)
            visit(next(r0,i));

        // sanity check on size and branching factor
        if (first) {
            val expSize = b0 / (1.0 - q*m);
            val obsBranch = (sumb to double) / size;
            val expBranch = q * m;
            System.out.printf("exp size / obs size: %.3f\n", expSize/size);
            System.out.printf("exp branching / obs branching: %.3f\n", expBranch / obsBranch);
        } 
        first = false;

        // should always get same size tree
        return size to double;
    }


    //
    // boilerplate
    //

    def this(args:Rail[String]) {
        super(args);
        reference("snakehead", "java", 4.41421e+06);
        reference("snakehead", "x10-opt-java", 2.87638e+06);
    }

    public static def main(args:Rail[String]) {
        new SeqUTSBin1(args).execute();
    }

}
