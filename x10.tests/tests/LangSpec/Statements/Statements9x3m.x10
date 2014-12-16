/* Current test harness gets confused by packages, but it would be in package Statements9x3m;
*/
// Warning: This file is auto-generated from the TeX source of the language spec.
// If you need it changed, work with the specification writers.


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



public class Statements9x3m extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Statements9x3m().execute();
    }


// file Statements line 953

 static class Example {
 static   class ThisExn extends Exception {}
 static   class ThatExn extends Exception {}
  var didFinally : Boolean = false;
  def example(b:Boolean) {
    try {
       throw b ? new ThatExn() : new ThisExn();
    }
    catch(ThatExn) {return true;}
    catch(ThisExn) {return false;}
    finally {
       this.didFinally = true;
    }
  }
  static def doExample() {
    val e = new Example();
    assert e.example(true);
    assert e.didFinally == true;
  }
}

 static  class Hook { def run() { Example.doExample(); return true; } }

}
