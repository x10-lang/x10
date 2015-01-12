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

import harness.x10Test;

/*
 * A method contains async but no finish.
 * The finish call the method recursively.
 */

public class FinishCrossMethodMultiAsync extends x10Test{
    
    var value: long = 0;
    
    public def update(level:long){
        if(level > 0){
            async update(level - 1);
            update(level - 1);
        }
        else{
            atomic this.value++;
        }
    }

    public def run() {
        finish {
            async update(4);
        }
        Console.OUT.println("FinishCrossMethodMultiAsync: value = " + this.value);
        return value == 16;
    }

    public static def main(Rail[String]) {
        new FinishCrossMethodMultiAsync().execute();
    }
    
    
}