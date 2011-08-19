import java.util.Random;

import com.ibm.apgas.Pool;
import com.ibm.apgas.Task;

public class MontyPi {

    public static double result = 0;
    
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: MontyPi <number of points:int>");
            return;
        }        
        final int N = Integer.parseInt(args[0]);
        Pool p = new Pool(new Task() {
            public void body() {
                doMontyPi(N);
              }
        });
        p.start();
    }

    static void doMontyPi(final int N) {
        final int P = Pool.numPlaces();
        final int W = N/P;
        System.out.println("P=" + P + " N=" + N+ " N/P="+W);
        long start = System.nanoTime();
        Pool.atEach(new Task() {
            public void body() {
                Random r = new Random();
                long hit = 0;
                for (int j=0; j < W; j++) {
                    double x = r.nextDouble();
                    double y = r.nextDouble();
                    if (x*x + y*y <= 1.0) hit++;
                }
                final long res = hit;
                Pool.runAsync(0, new Task() {
                    public void body() {
                        synchronized(MontyPi.class) {
                            result += res;
                        }
                    }});
            }
        });
        long stop = System.nanoTime();
        System.out.println("Pi = "+(4.0 * result / (double)N)+" in "+((double)(stop-start)/1e9)+" seconds");
    }
}
