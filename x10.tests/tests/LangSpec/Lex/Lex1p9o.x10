/* Current test harness gets confused by packages, but it would be in package Lex1p9o;
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
 *  (C) Copyright IBM Corporation 2006-2015.
 */

import harness.x10Test;



public class Lex1p9o extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(args:Rail[String]):void {
        new Lex1p9o().execute();
    }


// file Lex line 45

 static  class Backyquotey {
 def hokey(e:Long, a:Long, b:Long, d:Long, c:Long) = 0;
 def example() {
 val `while`=0; val `!`=0; val  `(unbalanced(`=0;  val `\`\\`=0; val `0` = 0;
 hokey(
`while`, `!`, `(unbalanced(`,  `\`\\`, `0`
 ); } }

 static class Hook {
   def run():Boolean = true;
}

}
