/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright Australian National University 2011.
 */
import harness.x10Test;
import x10.regionarray.*;

/**
 * Functional test of "at" closures with arrays 
 * of different base types (including cloning the array)
 * @author milthorpe 06/2011
 */
public class TestCloneArray extends x10Test {
    public val N : Int;
    public def this(N : Int) {
        this.N = N;
    }
    public def run(): Boolean {
        val N = this.N;

        // test Char
        val a = new Array[Char](N+1);
        for (i in 0..N) {
            a(i) = 'a';
        }
        at (here) {
            for (i in 0..N) {
                chk(a(i) == 'a');
            }
        }

        // test Int
        val b = new Array[Int](N+1);
        for (i in 0..N) {
            b(i) = i as int;
        }
        at (here) {
            var sum : Int = 0n;
            for (i in 0..N) {
                sum += b(i);
            }
            val expected = N*(N+1n)/2n;
            chk(sum == expected);
        }

        // test Double
        val c = new Array[Double](N+1);
        for (i in 0..N) {
            c(i) = i as Double;
        }
        at (here) {
            var sum : Double = 0.0;
            for (i in 0..N) {
                sum += c(i);
            }
            val expected = ((N*(N+1)/2) as Double);
            chk(sum == expected);
        }

        // test Complex
        val d = new Array[Complex](N+1);
        for (i in 0..N) {
            d(i) = Complex(i as Double, i as Double);
        }
        at (here) {
            var sum : Complex = Complex.ZERO;
            for (i in 0..N) {
                sum += d(i);
            }
            val expected = new Complex(((N*(N+1)/2) as Double), ((N*(N+1)/2) as Double));
            chk(sum == expected);
        }

        return true;
    }

    public static def main(var args: Rail[String]): void {
        var n : Int = 10n;
        if (args.size > 0) {
            n = Int.parseInt(args(0));
        }
        new TestCloneArray(n).execute();
    }

}
