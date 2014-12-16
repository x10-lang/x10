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


/**
 * @author vj 11/2013
 */
public class IsRef[T]{T isref} extends x10Test {
    static interface I{}
    static struct S implements I {}
    static class U[TT]{TT <: I} {
       val x = new IsRef[TT]();
    }
    public def run():Boolean {
      type II = (Int)=>Int;
       new IsRef[I]();
       new IsRef[(Int)=>Int]();
       new IsRef[I{self!=null}]();
       new IsRef[II{self!=null}]();
       new IsRef[IsRef[II]]();
       new IsRef[IsRef[II]{self!=null}]();
       return true;
    }

    public static def main(Rail[String]) {
        new IsRef[I]().execute();
    }

}
