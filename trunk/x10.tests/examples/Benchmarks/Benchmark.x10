// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

import harness.x10Test;

import x10.io.StringWriter;
import x10.io.Printer;

import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * @author bdlucas
 */

abstract class Benchmark extends x10Test {

    abstract def once(): double;
    abstract def expected(): double;
    abstract def operations(): double;

    def now() = (System.nanoTime() to double) * 1e-9;
    val out = System.out;

    @Native("java", "\"Java\"")
    @Native("c++", "String::Lit(\"C++\")")
    const TYPE = "";

    const WARMUP = 10.0;  // Java warmup time in secs
    const TIMING = 10.0;  // how long to run tests in secs

    public def run():boolean {

        // functional check
        out.println("functional check");
        val warmup = now();
        val result = once();
        if (result!=expected()) {
            out.println("got " + result + "; expected " + expected());
            return false;
        }

        // if Java do warmup
        if (TYPE.equals("Java")) {
            out.println("warmup for >" + WARMUP + "s");
            while (now()-warmup < WARMUP)
                once();
        }
        
        // run tests
        out.println("timing for >" + TIMING + "s");
        var time:double = 0.0;
        var min:double = double.POSITIVE_INFINITY;
        var reps:int = 0;
        while (time < TIMING) {
            val start = now();
            once();
            val t = now() - start;
            if (t<min)
                min = t;
            time += t;
            reps++;
        }
        time /= reps;

        // print info
        val ops = operations() / time;
        out.printf("time: %.3f; reps: %d; min/time: %.2f\n", time, reps, min/time);
        if (ops<1e6)      out.printf("%.3g kop/s\n", ops/1e3);
        else if (ops<1e9) out.printf("%.3g Mop/s\n", ops/1e6);
        else              out.printf("%.3g Gop/s\n", ops/1e9);
        out.printf("test=%s perf=%g\n", className(), ops);
            
        // test succeeded
        return true;
    }
}
