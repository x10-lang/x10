abstract public class Benchmark {

    abstract double once();
    abstract double expected();
    abstract double operations();

    int reps = 10;

    double now() {
        return System.nanoTime() * 1e-9;
    }

    void pr(String s) {
        System.out.println(s);
    }

    void run() {
        boolean first = true;
        double min = Double.POSITIVE_INFINITY;
        for (int i=0; i<reps; i++) {
            double start = now();
            double x = once();
            double time = now() - start;
            if (time<min) {
                min = time;
                i = 0;
            }
            System.out.printf("time %.3f, min %.3f\n", time, min);
            if (first && x!=expected()) {
                pr("got " + x + " expected " + expected());
                System.exit(-1);
            }
            first = false;
        }
        System.out.printf("op/s: %.5e\n", operations() / min);
    }
}
