/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

public class XClass {
    private static val time = System.nanoTime();
    def xint() = 100;
    def xobject() = new Object();
    def xstring() = "xyz";
    def xclass() = new XClass();
    def xthrows() { throw new Throwable(); }
    def xstaticfield() {
        finish for (p in Place.places()) {
            async at (p) Console.OUT.println("Time at place " + p.id + " is " + time);
        }
    }
}
