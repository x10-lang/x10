/**
 * Version of Stream with a collection of local arrays implementing a
 * global array.
 */

public class SeqStream1 extends Benchmark {

    final double alpha = 1.5;
    final double beta = 2.5;
    final double gamma = 3.0;

    final int NUM_TIMES = 10;
    final int PARALLELISM = 2;
    final int localSize = 512*1024;

    public double operations() {return 1.0 * localSize * PARALLELISM * NUM_TIMES;}
    public double expected() {return (localSize+1)*(alpha+gamma*beta);}

    //
    //
    //

    final double [][] as = new double[PARALLELISM][];
    final double [][] bs = new double[PARALLELISM][];
    final double [][] cs = new double[PARALLELISM][];

    {
        for (int p=0; p<PARALLELISM; p++) {
            as[p] = new double[localSize];
            bs[p] = new double[localSize];
            cs[p] = new double[localSize];
            for (int i=0; i<localSize; i++) {
                bs[p][i] = alpha*(p*localSize+i);
                cs[p][i] = beta*(p*localSize+i);
            }
        }
    }

    public double once() {
        for (int p=0; p<PARALLELISM; p++) {
            final double [] a = as[p];
            final double [] b = bs[p];
            final double [] c = cs[p];
            for (int t=0; t<NUM_TIMES; t++)
                for (int i=0; i<localSize; i++)
                    a[i] = b[i] + gamma*c[i];
        }
        return as[1][1];
    }

    //
    //
    //

    public static void main(String [] args) {
        new SeqStream1().run();
    }

}
