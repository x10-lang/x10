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

class LocalClass[T1,T2](p1:Empty, p2:Empty) {p1==p2} extends x10Test {
    f1:T1;
    f2:T2;
    public def this (f1:T1, f2:T2) {
        val x = new Empty();
        property(x,x);
        this.f1 = f1;
        this.f2 = f2;
    }

    public static abstract class Base[B1,B2,B3,B4,B5,B6] {
        f1:B1;
        f2:B2;
        f3:B3;
        f4:B4;
        f5:B5;
        f6:B6;
        def this (f1:B1, f2:B2, f3:B3, f4:B4, f5:B5, f6:B6) {
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
            this.f4 = f4;
            this.f5 = f5;
            this.f6 = f6;
        }
    }

    def meth1[M1,M2](m1:M1, m2:M2) {
        class Local[L1,L2] extends Base[T1,T2,M1,M2,L1,L2] {
            f1:T1;
            f2:T2;
            f3:M1;
            f4:M2;
            f5:L1;
            f6:L2;
            def this (f1:T1, f2:T2, f3:M1, f4:M2, f5:L1, f6:L2) {
                super(f1,f2,f3,f4,f5,f6);
                this.f1 = f1;
                this.f2 = f2;
                this.f3 = f3;
                this.f4 = f4;
                this.f5 = f5;
                this.f6 = f6;
            }
        }
        return new Local[M1,M2](f1,f2,m1,m2,m1,m2);
    }
    def meth1a[M1,M2](m1:M1, m2:M2) {
        return new Base[T1,T2,M1,M2,M1,M2](f1,f2,m1,m2,m1,m2) {
        };
    }


    public static abstract class PropertiesBase(b1:Empty, b2:Empty) { b1==b2 } {
    }

    def meth2[M1,M2](m1:M1, m2:M2) {
        class Local[L1,L2] extends PropertiesBase {
            public def this() {
                super(p1,p2);
            }
        }
        return new Local[T1,T2]();
    }


    public static abstract class ConstrainedBase[B1,B2,B3,B4,B5,B6](b1:Empty,b2:Empty) { b1==b2 } {
        f1:B1;
        f2:B2;
        f3:B3;
        f4:B4;
        f5:B5;
        f6:B6;
        def this (f1:B1, f2:B2, f3:B3, f4:B4, f5:B5, f6:B6, b1:Empty, b2:Empty) {b1==b2} {
            property(b1,b2);
            this.f1 = f1;
            this.f2 = f2;
            this.f3 = f3;
            this.f4 = f4;
            this.f5 = f5;
            this.f6 = f6;
        }
    }

    def meth3[M1,M2](m1:M1, m2:M2) { m1 == m2 } {
        class Local[L1,L2](lp1:Empty,lp2:Empty) {lp1==lp2} extends ConstrainedBase[T1,T2,M1,M2,L1,L2] {
            f1:L1;
            f2:L2;
            public def this (f1:L1,f2:L2,lp1:Empty,lp2:Empty) { lp1 == lp2 } {
                super(LocalClass.this.f1, LocalClass.this.f2, m1, m2, f1, f2, lp1, lp2);
                property(lp1,lp2);
                this.f1 = f1;
                this.f2 = f2;
            }

        }
        return new Local[T1,T2](f1, f2, p1, p2);
    }

    public static def main (args : Rail[String]) {
        new LocalClass[String,Int]("foo", 4n).execute();
    }

    public def run() {
        meth1[Float,Float](1.0f, 1.0f);
        meth2[Float,Float](1.0f, 1.0f);
        meth3[Float,Float](1.0f, 1.0f);
        return true;
    }

}

// vim: shiftwidth=4:tabstop=4:expandtab

