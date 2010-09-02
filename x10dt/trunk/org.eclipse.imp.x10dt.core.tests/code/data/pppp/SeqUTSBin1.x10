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

import x10.io.Console;

class SeqUTSBin1 extends Benchmark {
    const r0 = 0;                      // seed for root

    const b0 = 50000;                  // branching factor of root node
    const q = 0.12;                    // prob of non-zero branching factor
    const m = 8;                       // branching factor is m with prob q
    def expected() = 1234872.0;        // expected size given above params 

    def operations() = size as double; // work is proportional to size
    var size:int = 0;
    var sumb:int = 0;

    def visit(r:UTSRand.descriptor) {
        val x = UTSRand.number(r);
        val b = x<q? m : 0; // binomial distribution
        sumb += b;
        size++;
        for (var i:int=0; i<b; i++)
            visit(UTSRand.next(r,i));
    }
    var first:boolean = true;
    def once() {
        // root node
        size = 0;
        sumb = 0;
        for (var i:int=0; i<b0; i++)
            visit(UTSRand.next(r0,i));

        // sanity check on size and branching factor
        if (first) {
            val expSize = b0 / (1.0 - q*m);
            val obsBranch = (sumb as double) / size;
            val expBranch = q * m;
            x10.io.Console.OUT.println("exp size / obs size: " 
				       + (expSize/size));
            x10.io.Console.OUT.println("exp branching / obs branching: " 
				       + (expBranch / obsBranch));
        } 
        first = false;
        // should always get same size tree
        return size as double;
    }
    public static def main(Rail[String]) {
        new SeqUTSBin1().run();
    }
    public def typeName():String="SeqUTSBin1.x10";

}
