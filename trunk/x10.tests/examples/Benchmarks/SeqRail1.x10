// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqRail1 extends Benchmark {

    //
    // parameters
    //

    val N = 10000000;

    def expected() = N to double;

    def operations() = N to double;


    //
    // the benchmark
    //

    val a = Rail.makeVar[double](N, (nat)=>1.0);

    def once() {
        var sum: double = 0.0;
        for (var i:int=0; i<N; i++)
            sum += a(i);
        return sum;
    }

    //
    // boilerplate
    //

    def this(args:Rail[String]) {
        super(args);
        reference("snakehead", "java",             2.82138e+08);
        reference("snakehead", "x10-opt-java",     6.23501e+07);
        reference("snakehead", "x10-dbg-java",     1.64563e+05);
    }

    public static def main(args:Rail[String]) {
        new SeqRail1(args).execute();
    }
}
