// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * Basic array, X10-style loop
 *
 * @author bdlucas
 */

public class SeqArray2b extends Benchmark {

    //
    // parameters
    //

    val N = 2000;
    def expected() = 1.0*N*N*(N-1);
    def operations() = 2.0*N*N;

    //
    // the benchmark
    //

    val a = Array.makeFast[double]([0..N-1, 0..N-1], (Point)=>0.0) as Array[double](2){rect};

    def once() {
        for ((i,j):Point(2) in a)
            a(i,j) = (i+j) as double;
        var sum:double = 0.0;
        for ((i,j):Point(2) in a)
            sum += a(i,j);
        return sum;
    }

    //
    // boilerplate
    //

    public static def main(Rail[String]) {
        new SeqArray2b().execute();
    }
}
