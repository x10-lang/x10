/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

import harness.x10Test;

import x10.io.Printer;

import x10.compiler.Native;
import x10.compiler.NativeRep;


/**
 * @author bdlucas
 */

import x10.io.Printer;
import x10.io.Console;

abstract class Benchmark extends x10Test {

    abstract def once(): double;
    abstract def expected(): double;
    abstract def operations(): double;

    def now() = (System.nanoTime() as double) * 1e-9;
    val out:Printer;

    @Native("java", "\"java\"")
    @Native("c++", "x10::lang::String::Lit(\"cpp\")")
    static lg:String = "";

    static WARMUP = 60.0;  // java warmup time in secs
    static TIMING = 10.0;  // how long to run tests in secs

    def this() {
        out = x10.io.Console.OUT;
    }

    public def run():boolean {

        // functional check
        out.println("functional check");
        val warmup = now();
        val result = once();
        if (result!=expected()) {
            out.println("got " + result + "; expected " + expected());
            return false;
        }

        // if java do warmup
        if (lg.equals("java")) {
            out.println("warmup for >" + WARMUP + "s");
            while (now()-warmup < WARMUP)
                once();
        }
        
        // run tests
        out.println("timing for >" + TIMING + "s");
        var avg:double = 0.0;
        var min:double = double.POSITIVE_INFINITY;
        var count:int = 0n;
        while (avg < TIMING) {
            val start = now();
            once();
            val t = now() - start;
            if (t<min)
                min = t;
            avg += t;
            count++;
        }
        avg /= count;

        // print info
        val ops = operations() / avg;
        out.printf("time: %.3f; count: %d; min/time: %.2f\n", avg, count, min/avg);
        if (ops<1e6)      out.printf("%.3g kop/s\n", ops/1e3);
        else if (ops<1e9) out.printf("%.3g Mop/s\n", ops/1e6);
        else              out.printf("%.3g Gop/s\n", ops/1e9);
        out.printf("test=%s lg=x10-%s ops=%g\n", typeName(), lg, ops);
            
        // test succeeded
        return true;
    }
}
