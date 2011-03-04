// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * From the paper:
 *
 * The unbalanced tree search (UTS) problem is to count the number of
 * nodes in an implicitly constructed tree that is parameterized in
 * shape, depth, size, and imbalance.
 *
 * The nodes in a geometric tree have a branching factor that follows
 * a geometric distribution with an expected value that is specified
 * by the parameter b0 > 1.  The parameter d specifies its maximum
 * depth cut-off, beyond which the tree is not allowed to grow and
 * without which its growth would become intractable.  The expected
 * size of these trees is O((b0)^d), but since the geometric
 * distribution has a long tail, some nodes will have significantly
 * more than b0 children, yielding unbalanced trees. Unlike binomial
 * trees, the expected size of the subtree rooted at a node increases
 * with proximity to the root.
 * 
 * @author bdlucas
 */

import x10.util.Random;

import x10.compiler.Native;
import x10.compiler.NativeRep;


class SeqUTSGeo1 extends Benchmark {

    //
    // parameters
    //

    const r0 = 0;                      // seed for root

    const b0 = 4;                      // average branching factor
    const d = 10;                      // max depth
    def expected() = 906930.0;         // expected size given above params 

    def operations() = size as double; // work is proportional to size

    //
    // the benchmark
    //
    // node visitor
    //

    var size:int = 0;
    var sumb:int = 0;

    def visit(r:UTSRand.descriptor, depth:int) {
        val x = UTSRand.number(r);
        val q = b0 / (1.0 + b0);
        val b = (Math.log(x) / Math.log(q)) as int; // geometric distribution
        size++;
        sumb += b;
        if (depth<d)
            for (var i:int=0; i<b; i++)
                visit(UTSRand.next(r,i), depth+1);
    }
    
    //
    // do a tree
    //

    var first:boolean = true;

    def once() {

        // root node
        size = 0;
        sumb = 0;
        visit(r0, 0);

        // sanity check on size and branching factor
        if (first) {
            val obsBranch = (sumb as double) / size;
            val balancedSize = (Math.pow(b0, d+1)-1) / (obsBranch-1);
//            x10.io.Console.OUT.printf("balancedSize / size %.2f\n", balancedSize / size);
//            x10.io.Console.OUT.printf("obsBranch / b0 %.2f\n", obsBranch / b0);
            x10.io.Console.OUT.println("balancedSize / size " + (balancedSize / size));
            x10.io.Console.OUT.println("obsBranch / b0 " + (obsBranch / b0));
        } 
        first = false;

        // should always get same size tree
        return size as double;
    }


    //
    // boilerplate
    //

    public static def main(Rail[String]) {
        new SeqUTSGeo1().execute();
    }

}
