// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

/**
 * @author bdlucas
 */

public class SeqMethodCall1 extends Benchmark {

    val N = 2000000;
    def expected() = N to double;
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
        for (var i:int=0; i<N; i++) {
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

    def this(args:Rail[String]) {
        super(args);
        reference("snakehead", "java",             1.60307e+08);
        reference("snakehead", "x10-opt-java",     1.56158e+07);
        reference("snakehead", "x10-dbg-java",     1.99430e+05);
    }

    public static def main(args:Rail[String]) {
        new SeqMethodCall1(args).execute();
    }
}


