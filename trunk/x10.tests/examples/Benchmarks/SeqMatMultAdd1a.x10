// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqMatMultAdd1a extends Benchmark {

    val N = 100;
    def expected() = -288346.0;
    def operations() = N*N*N to double;

    //
    //
    //

    val r = [0..N-1,0..N-1] to Region;
    val a = Array.make[double](r, (p:Point)=>p(0)*p(1)/(N to double));
    val b = Array.make[double](r, (p:Point)=>p(0)-p(1) to double);
    val c = Array.make[double](r, (p:Point)=>p(0)+p(1) to double);

    def once() {
        for (var i:int=0; i<N; i++)
            for (var j:int=0; j<N; j++)
                for (var k:int=0; k<N; k++)
                    a(i,j) += b(i,k)*c(k,j);
        return a(20,20);
    }


    //
    //
    //

    public static def main(Rail[String]) {
        new SeqMatMultAdd1a().execute();
    }
}
