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

import harness.x10Test;

/**
 * @author hhorii 02/2010
 */

class XTENLANG_639 extends x10Test {

    public static def yes(b:Boolean, s:String) {
        if (!b) x10.io.Console.OUT.println("Oh, no!  "+ s);
        else x10.io.Console.OUT.println("YES: " + s);
    }
    public def test() {
        yes(A[Pare] :> B[Pare], "A[Pare] :> B[Pare] by inheritance");
        yes(A[Chil] :> B[Chil], "A[Chil] :> B[Chil] by inheritance");
        val chil : Chil = new Chil();
        val pare : Pare = new Pare();

        //FAILS: val xap : A[Pare] = new A[Pare](chil);
        val xap : A[Pare] = new A[Pare](pare);         
        val xbp : A[Pare] = new B[Pare](pare);

        // FAILS: val ap : A[Pare] = new B[Chil](chil);
        //val bc : B[Chil] = new B[Chil](chil);
        // val ac : A[Chil] = bc;
        //FAILS: val bp : B[Pare] = bc;
        //FAILS: val ap : A[Pare] = ac;
        //yes(ap.whut().equals("bChil"), "whut is it");
    }

    class A[X]{
        val x : X;
        def this(x:X){this.x=x;}
        def whut() = "a" + x.typeName();
    }

    class B[X] extends A[X] {
        def this(x:X) { super(x); }
        def whut() = "b" + x.typeName();
    }

    class Pare {}

    class Chil extends Pare {}

    public def run(): boolean {
        test();
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_639().execute();
    }
}

