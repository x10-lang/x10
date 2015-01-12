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
import x10.util.*;
import harness.x10Test;

/*
A somewhat more involved use of an inner class which refers to its
outer class's generic type parameter.

This test just has to compile. 
*/

public class GenericInnerClass2[T] extends x10Test {
  public static def main(Rail[String]){
     val p = new GenericInnerClass2[Int](3n);
     p.execute();
  }
  public def run():Boolean {
     return true;
  }
  
  val outerT: T;
  var outerVar : T;
  def this(t:T) {
    outerT = t;
    outerVar = t;
  }
  
  static interface SurpriseInterface[U] {
    def whistle(): U;
    def brag(u:U): void;
  }
  
  class Inner implements SurpriseInterface[T]{
    public def whistle(): T = outerT;
    public def brag(t:T) { outerVar = t;}
    val innerT : T;
    var innerVar : T;
    def this(t:T) { innerT = t; innerVar = t; }
  }
  
  class SubInner extends Inner {
    public def whistle() = innerT; // ought to be 
    public def brag(t:T) { innerVar = t; }
    def this(t:T) { super(t); }
  }
  
}
