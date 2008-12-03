// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqRail1 extends Benchmark {

    //
    // parameters
    //

    val N = 1000000;
    val M = 20;
    def expected() = N*M to double;
    def operations() = N*M to double;


    //
    // the benchmark
    //

    val a = Rail.makeVar[double](N+M, (nat)=>1.0);

    def once() {
        var sum: double = 0.0;
        for (var k:int=0; k<M; k++)
            for (var i:int=0; i<N; i++)
                sum += a(i+k);
        return sum;
    }

    //
    // boilerplate
    //

    def this(args:Rail[String]) {
        super(args);
        reference("snakehead", "java",             2.71974e+08);
        reference("snakehead", "x10-opt-java",     3.91420e+07);
        reference("snakehead", "x10-dbg-java",     1.64563e+05);
    }

    public static def main(args:Rail[String]) {
        new SeqRail1(args).execute();
    }
}
