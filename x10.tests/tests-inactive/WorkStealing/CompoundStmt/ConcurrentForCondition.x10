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
//OPTIONS: -WORK_STEALING=true

//For init has concurrent call

public class ConcurrentForCondition {
    static def fib(n:Int):Int {
        val t1:Int;
        val t2:Int;
        if (n < 2) return 1;
        finish {
            async t1 = fib(n-1);
            t2 = fib(n-2);
        }
        return t1 + t2;
    }
    
    
    public def run():boolean {
        var v:int = 0;
        for(var i:int = 0; i < fib(10) ; i++){
            v ++;
        }
    
        return v == 89;
    }

    public static def main(args:Rail[String]) {
        val r = new ConcurrentForCondition().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
}
