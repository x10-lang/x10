// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqMatMultAdd1a extends Benchmark {

    val N = 55*5;
    def expected() = -6866925.0;
    def operations() = N*N*N as double;

    //
    //
    //

    val r = [0..N-1,0..N-1] as Region;
    val a = Array.makeFast[double](r, (p:Point)=>p(0)*p(1) as double);
    val b = Array.makeFast[double](r, (p:Point)=>p(0)-p(1) as double);
    val c = Array.makeFast[double](r, (p:Point)=>p(0)+p(1) as double);

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                for (var k:int=0; k<N; k++)
                    a(i,j) += b(i,k)*c(k,j);
        return a(10,10);
    }


    //
    //
    //

    public static def main(Rail[String]) {
        new SeqMatMultAdd1a().execute();
    }
}
