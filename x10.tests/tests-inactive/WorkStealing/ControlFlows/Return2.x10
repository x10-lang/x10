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


//Return in sub frames, value is boolean

public class Return2 {
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
        {
            for(var i:int=0; i < 100; i++){
                val v:int;
                finish async v = fib(20);
                if(i + v > 50){
                    Console.OUT.println(">"+v);
                    return true;
                }
                else{
                    Console.OUT.println(v);
                }
            }
        }  
        return false;
    }

    public static def main(args:Rail[String]) {
        val r = new Return2().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
}
