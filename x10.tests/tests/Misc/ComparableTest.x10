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

/*
 * testcase for XTENLANG-3287
 */
public class ComparableTest extends x10Test {

    static class S implements Comparable[S] {
        var x:Int;
        def this(a:Int) { x = a; }
        static def xxx():void {}
        def yyy():void {}
        static def xxx2(a:S):void {}
        def yyy2(a:S):void {}
        public def compareTo(o:S) { o.x = 1001n; o.xxx(); o.yyy(); o.xxx2(o); o.yyy2(o); return x.compareTo(o.x); }
    }

    class MyComparable(s:Comparable[Int]) implements Comparable[Int] {
        public def compareTo(v:Int) = s.compareTo(v);
    }

    class MyComparable1(s:Comparable[Int]) {
        public def compareTo(v:Int) = s.compareTo(v);
    }
    class MyComparable2 extends MyComparable1 implements Comparable[Int] {
        def this(s:Comparable[Int]) {
            super(s);
        }
    }

    public def run(): boolean = {
        val s = 999n;
        
        val c = s as Comparable[Int];
        chk(c.compareTo(1000n) < 0n);

        val m = new MyComparable(s);
        chk(m.compareTo(1000n) < 0n);
        
        val m1 = new MyComparable1(s);
        chk(m1.compareTo(1000n) < 0n);
        
        val m2 = new MyComparable2(s);
        chk(m2.compareTo(1000n) < 0n);
        
        val m21:MyComparable1 = m2;
        chk(m21.compareTo(1000n) < 0n);

        val m2c:Comparable[Int] = m2;
        chk(m2c.compareTo(1000n) < 0n);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ComparableTest().execute();
    }
}
