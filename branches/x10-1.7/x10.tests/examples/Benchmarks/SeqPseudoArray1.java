// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqPseudoArray1 extends Benchmark {

    final int N = 2000;
    double expected() {return 1.0;}
    double operations() {return N*N;}

    //
    //
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
        
        final public void set(double v, int i0, int i1) {
            raw[i0*m1+i1] = v;
        }
        
        final public double apply(int i0, int i1) {
            return raw[i0*m1+i1];
        }
    }
    
    Arr a = new Arr(N, N);

    double once() {
        for (int i=0; i<N; i++)
            for (int j=0; j<N; j++)
                a.set(a.apply(i,j)+1, i,j);
        return a.apply(20,20);
    }

    //
    // boilerplate
    //

    public static void main(String [] args) {
        new SeqPseudoArray1().run();
    }
}


