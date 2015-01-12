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

public class Return1 {
    public def run():boolean {        
        {
            val v:int;
            finish async v = 1;
            if (v == 1) return true;
        }
        
        return false;
    }

    public static def main(args:Rail[String]) {
        val r = new Return1().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
}
