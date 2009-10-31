// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqPseudoArray1 extends Benchmark {

    //
    // parameters
    //

    val N = 2000;
    def expected() = 1.0;
    def operations() = N*N as double;


    //
    // the benchmark
    //

    final static class Arr {

        val m0: int;
        val m1: int;
        val raw: Rail[double]!;
        
        def this(m0:int, m1:int) {
            this.m0 = m0;
            this.m1 = m1;
            this.raw = Rail.makeVar[double](m0*m1);
        }
        
        final def set(v:double, i0: int, i1: int) {
            raw(i0*m1+i1) = v;
        }
        
        final public def apply(i0:int, i1: int) {
            return raw(i0*m1+i1);
        }
    }
    
    val a = new Arr(N, N);

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                a(i,j) += 1;
        return a.apply(20,20);
    }

    //
    // boilerplate
    //

    public static def main(Rail[String]) {
        new SeqPseudoArray1().execute();
    }
}
