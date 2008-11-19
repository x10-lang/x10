public class SeqRail1 extends Benchmark {

    final int N = 10000000;

    double expected() {return N;}

    double operations() {return N;}


    //
    //
    //

    final double [] a = new double[N];

    {for (int i=0; i<N; i++) a[i] = 1;}

    double once() {
        double sum = 0.0;
        for (int i=0; i<N; i++)
            sum += a[i];
        return sum;
    }



    //
    //
    //

    public static void main(String [] args) {
        new SeqRail1().run();
    }
}


