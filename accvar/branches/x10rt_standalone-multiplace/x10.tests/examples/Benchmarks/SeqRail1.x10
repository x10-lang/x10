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
    def expected() = N*M as double;
    def operations() = N*M as double;


    //
    // the benchmark
    //

    val a = Rail.make[double](N+M, (nat)=>1.0);

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

    public static def main(Rail[String]) {
        new SeqRail1().execute();
    }
}
