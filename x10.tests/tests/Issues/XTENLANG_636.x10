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
 * @author makinoy 04/2010
 */

public class XTENLANG_636 extends x10Test {

      interface ISet[X] { def set(x:X):void; }
      interface IGet[X] { def get():X; }

       class Cell[X] implements ISet[X], IGet[X] {
        var x : X;
        def this(x:X) { this.x = x; }
        public def get() : X = x;
        public def set(x:X) = {this.x = x;} 
      }

       class Get[X] implements IGet[X]{
        val x : X; // val fields are covariant
        def this(x:X) { this.x = x; }
        public def get() : X = x;
      }        

       class Set[X] implements ISet[X]{
        var x : X; // var fields must be invariant
        def this(x:X) { this.x = x; }
        public def set(x:X) = {this.x = x;} 
      }


      class Super  {
        public val name: String;
        def this(n:String) { name = n; }
        def equals(other:Super) = (this.typeName().equals(other.typeName()) && this.name.equals(other.name));
        }

      class Sub extends Super {
        def this(n:String) {super(n);}
      }
      
      
    public def run(): boolean = {
        val test1 = Sub <: Super;
        val test2 = ISet[Sub] :> ISet[Super];
        val test3 = ISet[Sub] <: ISet[Int];
        val test4 = ISet[Sub] :> ISet[Int];
        val test5 = ISet[Sub] == ISet[Int];
        return test1 && !test2 && (!test3) && (!test4) && (!test5);
    }

    public static def main(var args: Rail[String]): void = {
        new XTENLANG_636().execute();
    }
}
