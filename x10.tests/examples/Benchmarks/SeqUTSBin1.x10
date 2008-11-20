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

    //const b0 = 1000000;
    //const q = 0.2;
    //const m = 4;
    //def expected() = 4993764.0;

    const r0 = 0;                      // seed for root
    const b0 = 100000;                 // branching factor of root node
    const q = 0.12;                    // prob of non-zero branching factor
    const m = 8;                       // branching factor is m with prob q

    def expected() = 2433680.0;        // expected size given above params 
    def operations() = size to double; // work is proportional to size


    //
    // the benchmark
    //
    // For now use util.Random instead of SHA. To substitute SHA
    // redefine descriptor, next(), and number().
    //

    static type descriptor = long;

    def next(r:descriptor, i:nat) {
        val rand = new Random(r+i);
        for (var k:int=0; k<5; k++)
            rand.nextLong();
        return rand.nextLong();
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
        reference("snakehead", "java", 1.74059e+08);
        reference("snakehead", "x10-opt-java", 3.99802e+07);
    }

    public static def main(args:Rail[String]) {
        new SeqUTSBin1(args).execute();
    }

}
