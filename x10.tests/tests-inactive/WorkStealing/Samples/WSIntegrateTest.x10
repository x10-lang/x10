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

public class WSIntegrateTest {
    public def run():boolean {
        val res = Integrate.computeArea(0, 64);
        return (res > 4196352.000 && res < 4196352.001);
    }

    public static def main(args:Rail[String]) {
        val r = new WSIntegrateTest().run();
        if(r){
             x10.io.Console.OUT.println("++++++Test succeeded.");
        }
    }
}
