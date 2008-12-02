// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

final public class SeqMatMultAdd1 extends Benchmark {

    //
    // parameters
    //

    final int N = 100;
    double operations() {return N*N*N;}
    double expected() {return -288346.0;}
        

    //
    // the benchmark
    //

    final int Na = N;
    final int Nb = N;
    final int Nc = N;

    double [] a = new double[Na*Na];
    double [] b = new double[Nb*Nb];
    double [] c = new double[Nc*Nc];

    {
        for (int i=0; i<Na; i++) {
            for (int j=0; j<Na; j++) {
                a[i*Na+j] = i*j/((double)Na);
                b[i*Na+j] = i-j;
                c[i*Na+j] = i+j;
            }
        }
    }

    double once() {
        for (int i=0; i<Na; i++)
            for (int j=0; j<Nb; j++)
                for (int k=0; k<Nc; k++)
                    a[i*Na+j] += b[i*Nb+k]*c[k*Nc+j];
        return a[20*Na+20];
    }


    //
    // boilerplate
    //

    public static void main(String [] args) {
        new SeqMatMultAdd1().run();
    }
}
