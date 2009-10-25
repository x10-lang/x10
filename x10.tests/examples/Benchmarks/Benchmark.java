// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import java.io.*;


/**
 * @author bdlucas
 */

abstract public class Benchmark {

    abstract double once();
    abstract double expected();
    abstract double operations();

    double TIMING = 10.0;
    double WARMUP = 30.0;

    void run() {

        PrintStream out = System.out;
        String name = this.getClass().getName();

        // functional check;
        out.printf("functional check\n");
        double warmup = now();
        double result = once();
        if (result!=expected()) {
            out.printf("exected %f, result %f\n", expected(), result);
            System.exit(-1);
        }
        
        // do warmup
        out.println("warmup for >" + WARMUP + "s");
        while (now()-warmup < WARMUP)
            once();

        // run it for >TIMING secs
        out.println("timing for >" + TIMING + "s");
        double avg = 0.0;
        double min = 1e100;
        int count = 0;
        while (avg < TIMING) {
            double start = now();
            once();
            double t = now() - start;
            if (t<min)
                min = t;
            avg += t;
            count++;
        }
        avg /= count;
        
        // print info
        double ops = operations() / avg;
        out.printf("time: %.3f; count: %d; min/time: %.2f\n", avg, count, min/avg);
        if      (ops<1e6) out.printf("%.3g kop/s\n", ops/1e3);
        else if (ops<1e9) out.printf("%.3g Mop/s\n", ops/1e6);
        else              out.printf("%.3g Gop/s\n", ops/1e9);
        
        out.printf("test=%s lg=java ops=%g\n", name, ops);
    }

    double now() {
        return System.nanoTime() * 1e-9;
    }
}
