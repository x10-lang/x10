/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

import harness.x10Test;

/**
 * Tests many syntactic features of dep types.
 *
 * @author vj, 5/17/2006
 */
public class DepType(i:int, j:int) extends x10Test {

    // property declaration for an inner class.
    static class Test(k:int) extends DepType {
        def this(kk:int):Test = {
            super(3,4);
            property(kk);
        }
    }

    static class Test2 extends DepType {
        def this():Test2 = {
            super(3,5);
        }
    }

    // thisClause on a class, and extension of a deptyped class
    // This test is no longer legal. 3/2/2010. Cant use this in
    // returntype of a constructor.
    /*static class Test3 extends DepType {
        val k:int;
        def this(v:int):Test3{self.j==this.k} {
            super(v,v,v);
            k = v;
        }
    }*/

    // A constructor may specify constraints on properties that are true of the returned object.
    public def this(i:int, j:int):DepType{self.i==i && self.j==j} = {
        property(i,j);
    }

    //  method specifies a thisClause.
    public static def make(i: int{self==3}):DepType{self.i==3,self.j==3} = new DepType(i,i);

    // a local variable with a dep clause.
    public  def  run():boolean = {
        val d:DepType{self.i==3} = new DepType(3,6);
        return true;
    }

    //  a method whose return type is a deptype
    public def run3():boolean{self==true} = {
        x10.io.Console.OUT.println("i (=3?) = " + i);
        return true;
    }
    public def run4( j:int):boolean = {
        x10.io.Console.OUT.println("i (=3?) = " + i);
        return true;
    }
    public static def main(args: Array[String](1)):void = {
        new DepType(3,9).execute();
    }
}
