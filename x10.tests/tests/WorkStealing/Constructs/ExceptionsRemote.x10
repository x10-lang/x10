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
 * Test for remote x10 exceptions in X10 work-stealing
 */
public class ExceptionsRemote extends x10Test {
    static def e(msg:String): long = {
        if (true) throw new x10.lang.Exception(msg);
        return -1;
    }
    
    var ok: boolean = true;

    public def run(): boolean = {

        try {
            finish {
                async e("LOCAL");
                async at(Place.places().next(here)) e("REMOTE");
            }
        }
        catch(e: x10.lang.MultipleExceptions){
            //correct
            val es = e.exceptions();
            Console.OUT.println("In multiple exception handling: size =" + es.size);
            ok &= (es.size == 2L);
            //should capture 3 & 4
            var ok1:boolean = false;
            var ok2:boolean = false;
            for(var i:int =0n; i<es.size; i++){
                if(es(i).getMessage().equals("LOCAL")){
                    Console.OUT.println("Local exception was caught");
                    ok1 = true;
                }
                else if(es(i).getMessage().equals("REMOTE")){
                    Console.OUT.println("Remote exception was caught");
                    ok2 = true;
                }
            }
            ok &= (ok1 && ok2);
        }
        catch(e: x10.lang.Exception){
            //incorrect
            Console.OUT.println("Async exception is not merged in finish scope");
            ok = false;
        }

        return ok;
    }

    public static def main(Rail[String]) {
        new ExceptionsRemote().execute();
    }
}
