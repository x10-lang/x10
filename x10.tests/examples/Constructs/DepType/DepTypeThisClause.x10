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
 * Checks that an arg i from the param list of one method does not stray into
 * body of another.
 *
 * @author vj, 5/17/2006
 */

public class DepTypeThisClause(i:int, j:int) extends x10Test {
    def this(ii:int, jj:int):DepTypeThisClause{self.i==ii,self.j==jj} = { property(ii,jj); }

    // i is a param for this method and also a property
    def make(i:int(3)){this.i==3}:DepTypeThisClause = new DepTypeThisClause(i,i);

/* Originally, this was: 
    // a method whose return type is a deptype
    public def run(){this.i==3}:boolean(true) = {
        x10.io.Console.OUT.println("i (=3?) = " + i); //property ref.
        return true;
    }
  But that didn't work for reasons unrelated to the test.
/Users/bard/x10/x10-trunk/x10.tests/examples/Constructs/DepType/DepTypeThisClause.x10:25-28: run(){this12935.i==3}[]: x10.lang.Boolean{_self12939==true} in DepTypeThisClause cannot override run(): x10.lang.Boolean in harness.x10Test; method guard is not entailed.
*/

   public def run():boolean{
      val IAmThree : DepTypeThisClause{self.i==3} = new DepTypeThisClause(3,171); 
      val ret : boolean(true) = IAmThree.NotReallyRun();
      return ret;
   }

    public def NotReallyRun(){this.i==3}:boolean(true) = {
        x10.io.Console.OUT.println("i (=3?) = " + i); //property ref.
        return true;
    }


    public static def main(Rail[String]) = {
        new DepTypeThisClause(3,9).execute();
    }

}
