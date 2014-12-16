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
 * Checks that an arg i from the param list of one method does not stray into
 * body of another.
 *
 * @author vj, 5/17/2006
 */

public class DepTypeThisClause(i:int, j:int) extends x10Test {
    def this(ii:int, jj:int):DepTypeThisClause{self.i==ii,self.j==jj} = { property(ii,jj); }

    // i is a param for this method and also a property
    def make(i:int(3n)){this.i==3n}:DepTypeThisClause = new DepTypeThisClause(i,i);

   public def run():boolean{
      val IAmThree : DepTypeThisClause{self.i==3n} = new DepTypeThisClause(3n,171n); 
      val ret : boolean(true) = IAmThree.NotReallyRun();
      return ret;
   }

    public def NotReallyRun(){this.i==3n}:boolean(true) = {
        x10.io.Console.OUT.println("i (=3?) = " + i); //property ref.
        return true;
    }


    public static def main(Rail[String]) = {
        new DepTypeThisClause(3n,9n).execute();
    }

}
