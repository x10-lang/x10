/* Current test harness gets confused by packages, but it would be in package Expressions_ArrayCtor_Details;
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

// file Expressions line 923
class Eel{}
class Example{
def example(){
val zero <: Array[Int{self == 0}](1) = new Array[Int{self == 0}][0];
val non1 <: Array[Int{self != 1}](1) = new Array[Int{self != 1}][0];
val eels <: Array[Eel{self != null}](1) =
    new Array[Eel{self != null}][ new Eel() ];
}}

class Hook {
   def run():Boolean = true;
}


public class Expressions17 extends x10Test {
   public def run() : boolean = (new Hook()).run();
   public static def main(var args: Array[String](1)): void = {
        new Expressions17().execute();
    }
}    
