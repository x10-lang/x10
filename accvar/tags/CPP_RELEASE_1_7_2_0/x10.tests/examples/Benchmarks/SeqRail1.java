// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqRail1 extends Benchmark {

    final int N = 1000000;
    final int M = 20;
    double expected() {return N*M;}
    double operations() {return N*M;}


    //
    //
    //

    final double [] a = new double[N+M];

    {for (int i=0; i<N+M; i++) a[i] = 1;}

    double once() {
        double sum = 0.0;
        for (int k=0; k<M; k++)
            for (int i=0; i<N; i++)
                sum += a[i+k];
        return sum;
    }



    //
    //
    //

    public static void main(String [] args) {
        new SeqRail1().run();
    }
}


