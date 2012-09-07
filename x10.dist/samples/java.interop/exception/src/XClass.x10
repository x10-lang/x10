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

public class XClass {   
    public static def main(args:Array[String](1)):void {
        var allPassed: Boolean = true;
        var passed: Boolean;
        var DEBUG: Boolean = false;
        if (args.size > 0 && args(0).equalsIgnoreCase("true")) { DEBUG = true; }

        passed = false;
        try {
            JClass.throwable("throwable");
        } catch (e:x10.lang.CheckedThrowable) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }
        passed = false;
        try {
            JClass.exception("exception");
        } catch (e:x10.lang.CheckedException) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }
        passed = false;
        try {
            JClass.runtimeException("runtimeException");
        } catch (e:x10.lang.Exception) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }
        passed = false;
        try {
            JClass.error("error");
        } catch (e:x10.lang.Error) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }

        passed = false;
        try {
            JClass.aThrowable("aThrowable");
        } catch (e:x10.lang.CheckedThrowable) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }
        passed = false;
        try {
            JClass.anException("anException");
        } catch (e:x10.lang.CheckedException) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }
        passed = false;
        try {
            JClass.aRuntimeException("aRuntimeException");
        } catch (e:x10.lang.Exception) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }
        passed = false;
        try {
            JClass.anError("anError");
        } catch (e:x10.lang.Error) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }

        passed = false;
        try {
            throw new x10.lang.Exception("x10exception");
        } catch (e:x10.lang.CheckedThrowable) {
            passed = true;
            if (DEBUG) e.printStackTrace();
        } finally {
            allPassed &= passed;
            assert passed;
        }

        if (allPassed) {
            Console.OUT.println("Passed All tests.");
        } else {
            Console.OUT.println("Failed some tests");
        }
    }
}
