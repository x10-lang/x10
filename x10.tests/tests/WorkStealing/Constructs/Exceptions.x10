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

/**
 * Test for x10 exceptions in X10 work-stealing
 */
public class Exceptions extends x10Test {
    static def e(msg:String): long = {
        if (true) throw new x10.lang.Exception(msg);
        return -1;
    }
    
    var ok: boolean = true;

    public def run(): boolean = {
        //step 1: finish try-catch can catch non async exception

        finish {
            try {
                async Console.OUT.println("In Async");
                e("STEP1");
            }
            catch(e: x10.lang.Exception){
                if(e.getMessage().equals("STEP1")){
                    Console.OUT.println("Non-Async exception catpured");
                    ok &= true;
                }
                else{
                    ok &= false;
                }
            }
        }

        //step 2: exception's final block should be executed
        finish {
            try {
                ok = false;
                async Console.OUT.println("In Async 2");
                try{
                    e("STEP2");
                }
                catch(e: x10.lang.Exception){
                    throw e;
                }
                finally{
                    ok = true;
                    Console.OUT.println("Non-Async exception finnaly executed");
                }
            }
            catch(e: x10.lang.Exception){
                if(e.getMessage().equals("STEP2")){
                    ok &= true;
                }
                else{
                    ok &= false;
                }
            }
        }
        
        //step 3 async exception should be captured as MultipleExceptions
        try {
            finish {
                async e("STEP3");
                e("STEP4");
            }
        }
        catch(e: x10.lang.MultipleExceptions){
            //correct
            val es = e.exceptions();
            Console.OUT.println("In multiple exception handling: size =" + es.size);
            ok = (es.size == 2L);
            //should capture 3 & 4
            var ok3:boolean = false;
            var ok4:boolean = false;
            for(var i:int = 0n; i<es.size; i++){
                if(es(i).getMessage().equals("STEP3")){
                    ok3 = true;
                }
                else if(es(i).getMessage().equals("STEP4")){
                    ok4 = true;
                }
            }
            ok &= (ok3 && ok4);
        }
        catch(e: x10.lang.Exception){
            //incorrect
            Console.OUT.println("Async exception is not merged in finish scope");
            ok = false;
        }

        return ok;
    }

    public static def main(Rail[String]) {
        new Exceptions().execute();
    }
}
