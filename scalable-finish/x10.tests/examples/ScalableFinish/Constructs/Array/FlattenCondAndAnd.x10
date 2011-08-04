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

 

/**
 * Conditional and is evaluated, well, conditionally. So it must be
 * translated thus:
 *
   c = a && b
   =>
   <stmt-a>
   boolean result = a;
   if (a) {
     <stmt-b>;
     result = b;
     }
     c = result;
 *
 * @author vj
 */
 
public class FlattenCondAndAnd   {

    val a: Array[Boolean](2)!;

    public def this(): FlattenCondAndAnd = {
        a = new Array[Boolean]([1..10, 1..10], ((i,j): Point) => false);
    }

    def m(x: boolean)= x;

    public def run(): boolean = {
        var x: boolean = (m(a(1, 1)) && a(0, 0))&& a(-1, -1); // the second expression will throw an exception if it is evaluated.
        return !x;
    }

    public static def main(var args: Rail[String]): void = {
        new FlattenCondAndAnd().run ();
    }
    
}