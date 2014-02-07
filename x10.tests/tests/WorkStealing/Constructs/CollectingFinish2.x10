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
//OPTIONS: -WORK_STEALING=true


import harness.x10Test;
/*
 * Collecting-Finish with generics and x10 dividable loop
 */

class CollectingFinish2 extends x10Test {
    
    static struct Reducer implements Reducible[Long] {
        public def zero()=0;
        public operator this(a:Long,b:Long)=a+b;
    }
    
    public def sum[T](a:Reducible[T], n:T):T{
        val x = finish(a){
            for(1..1000){
                async offer n;
            }
        };
        return x;
    }
    
    public def run():boolean {
        val r = sum[long](Reducer(), 10);
        return (r == 10000);
    }
    
    public static def main(Rail[String]) {
        new CollectingFinish2().execute();
    }
    
}
