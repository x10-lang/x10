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

import harness.x10Test;


class FinishTest3 extends x10Test {

    def test():Boolean {
        var ok:Boolean = false;
        try {
            finish
            {
                throw new Error("Throw exception in " + here);
            }
        }
        catch (ex: MultipleExceptions) {
            ok = true;
        }
        return ok;
    }

    public def run(){
        return test();
    }

    public static def main(args: Rail[String]):void {
        new FinishTest3().execute();
    }

}
