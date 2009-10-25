// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqPseudoArray2a extends Benchmark {

    //
    // parameters
    //

    final int N = 2000;

    double expected() {return 1.0*N*N*(N-1);}
    double operations() {return 2.0*N*N;}


    //
    // the benchmark
    //

    final static class Arr {

        final int m0;
        final int m1;
        final double [] raw;

        Arr(int m0, int m1) {
            this.m0 = m0;
            this.m1 = m1;
            this.raw = new double[m0*m1];
        }
        
        void set(double v, int i0, int i1) {
            raw[i0*m1+i1] = v;
        }
        
        double apply(int i0, int i1) {
            return raw[i0*m1+i1];
        }
    }

    final Arr a = new Arr(N, N);

    double once() {
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                a.set(i+j, i,j);
        double sum = 0.0;
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                sum += a.apply(i,j);
        return sum;
    }

    //
    // boilerplate
    //

    public static void main(String [] args) {
        new SeqPseudoArray2a().run();
    }
}
