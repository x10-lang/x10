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

/**
 * @author bdlucas
 */

public class SeqMethodCall1 extends Benchmark {

    val N = 2000000;
    def expected() = N as double;
    def operations() = N * 5.0; // 5 method calls per iteration

    //
    //
    //

    final static class X {
        var x:double = 0.0;
        def foo(y:double) = (x+=y);
    }

    val x = new X();
    
    def once() {
        var sum:double = 0.0;
        val a:double = 0;
        val b:double = -1;
        val c:double = 1;
        val d:double = 2;
        val e:double = -2;
        for (1..N) {
            sum += x.foo(a);
            sum += x.foo(b);
            sum += x.foo(c);
            sum += x.foo(d);
            sum += x.foo(e);
        }
        return sum;
    }


    //
    //
    //

    public static def main(Rail[String]) {
        new SeqMethodCall1().execute();
    }
}


