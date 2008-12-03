// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

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

        // now run reps times and average the time, and compute new min
        min = Double.POSITIVE_INFINITY;
        double time = 0;
        if (reps>0) pr("--- averaging");
        for (int i=0; i<reps; i++) {
            double s = now();
            double result = once();
            double t = now() - s;
            if (t<min)
                min = t;
            System.out.printf("time %.3f, min %.3f\n", t, min);
            time += t;
        }            
        time = time / reps;

        System.out.printf("op/s: %.5e\n", operations() / time);
        System.out.printf("min/time: %.2f\n", min/time);
    }
}
