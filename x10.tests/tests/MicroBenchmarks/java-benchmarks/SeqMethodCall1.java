/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

/**
 * @author bdlucas
 */

public class SeqMethodCall1 extends Benchmark {

    final int N = 2000000;
    double expected() {return N;}
    double operations() {return N * 5.0;} // 5 method calls per iteration

    //
    //
    //

    final static class X {
        double x = 0.0;
        double foo(double y) {return x+=y;}
    }

    X x = new X();
    
    double once() {
        double sum = 0.0;
        double a = 0;
        double b = -1;
        double c = 1;
        double d = 2;
        double e = -2;
        for (int i=0; i<N; i++) {
            sum += x.foo(a);
            sum += x.foo(b);
            sum += x.foo(c);
            sum += x.foo(d);
            sum += x.foo(e);
        }
        return sum;
    }


    //
    // boilerplate
    //

    public static void main(String [] args) {
        new SeqMethodCall1().run();
    }
}


