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
//OPTIONS: -WORK_STEALING=true


import harness.x10Test;
/*
 * Collecting-Finish with generics and x10 dividable loop
 */

class CollectingFinish2 extends x10Test {
    
    static struct Reducer implements Reducible[Int] {
        public def zero()=0;
        public operator this(a:Int,b:Int)=a+b;
    }
    
    public def sum[T](a:Reducible[T], n:T):T{
        val x = finish(a){
            for(var i:int = 0; i < 1000; i++){
                async offer n;
            }
        };
        return x;
    }
    
    public def run():boolean {
        val r = sum[int](Reducer(), 10);
        return (r == 10000);
    }
    
    public static def main(Array[String](1)) {
        new CollectingFinish2().execute();
    }
    
}
