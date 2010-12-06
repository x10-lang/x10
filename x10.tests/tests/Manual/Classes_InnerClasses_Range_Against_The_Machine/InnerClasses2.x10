/* Current test harness gets confused by packages, but it would be in package Classes_InnerClasses_Range_Against_The_Machine;
*/

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

// file InnerClasses line 69
class Range(m:Int, n:Int) implements Iterable[Int]{
  public def iterator ()  = new RangeIter();
  private class RangeIter implements Iterator[Int] {
     private var n : Int = m;
     public def hasNext() = n <= Range.this.n;
     public def next() = n++;
  }
  public static def main(argv:Array[String](1)) {
    val r = new Range(3,5);
    for(i in r) Console.OUT.println("i=" + i);
  }
}

class Hook {
   def run():Boolean = true;
}


public class InnerClasses2 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new InnerClasses2().execute();
    }
}    
