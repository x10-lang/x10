/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

import harness.x10Test;


/**
 * Checked exceptions.
 * Valid for X10 2.3 and later.
 * 
 * @author mtake 7/2012
 */
public class CheckedExceptions1 extends x10Test {

    public def run():Boolean {
        var ok:Boolean = true;
        var res:Boolean;

        try {
            res = false;
            val e = new CheckedThrowable("hi");
            val typeName = e.typeName();
            if (!typeName.equals("x10.lang.CheckedThrowable")) {
                ok = false;
                Console.OUT.println("Error: incorrect typename: " + typeName);
            }
            if (!(e instanceof CheckedThrowable)) {
                ok = false;
                Console.OUT.println("Error: not subtype of CheckedThrowable: " + typeName);
            }
            throw e;
        } catch (e:CheckedThrowable) {
            res = true;
        } finally {
            if (!res) {
                ok = false;
                Console.OUT.println("Error: CheckedThrowable was not caught!");
            }
        }
        
        try {
            res = false;
            val e = new CheckedException("hi");
            val typeName = e.typeName();
            if (!typeName.equals("x10.lang.CheckedException")) {
                ok = false;
                Console.OUT.println("Error: incorrect typename: " + typeName);
            }
            if (!(e instanceof CheckedException)) {
                ok = false;
                Console.OUT.println("Error: not subtype of CheckedException: " + typeName);
            }
            throw e;
        } catch (e:CheckedException) {
            res = true;
        } finally {
            if (!res) {
                ok = false;
                Console.OUT.println("Error: CheckedException was not caught!");
            }
        }
        
        try {
            res = false;
            val e = new CheckedException("hi");
            val typeName = e.typeName();
            if (!typeName.equals("x10.lang.CheckedException")) {
                ok = false;
                Console.OUT.println("Error: incorrect typename: " + typeName);
            }
            if (!(e instanceof CheckedThrowable)) {
                ok = false;
                Console.OUT.println("Error: not subtype of CheckedThrowable: " + typeName);
            }
            throw e;
        } catch (e:CheckedThrowable) {
            res = true;
        } finally {
            if (!res) {
                ok = false;
                Console.OUT.println("Error: CheckedException was not caught as CheckedThrowable!");
            }
        }
        
        return ok;
    }

    public static def main(Array[String](1)) {
        new CheckedExceptions1().execute();
    }

}
