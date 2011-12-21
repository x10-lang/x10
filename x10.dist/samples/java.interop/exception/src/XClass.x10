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

import x10.compiler.Native;
import x10.interop.java.Throws;

public class XClass {
    public static def main(args:Array[String](1)):void {
	var allPassed: Boolean = true;
	var passed: Boolean;
	var DEBUG: Boolean = true;
	if (args.size > 0 && !args(0).equalsIgnoreCase("true")) { DEBUG = false; }

	passed = false;
	try {
	    try {
		JClass.throwable("throwable");
	    } catch (e:java.lang.Throwable) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		JClass.exception("exception");
	    } catch (e:java.lang.Exception) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		JClass.runtimeException("runtimeException");
	    } catch (e:java.lang.RuntimeException) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		JClass.error("error");
	    } catch (e:java.lang.Error) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}

	passed = false;
	try {
	    try {
		JClass.aThrowable("aThrowable");
	    } catch (e:java.lang.Throwable) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		JClass.anException("anException");
	    } catch (e:java.lang.Exception) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		JClass.aRuntimeException("aRuntimeException");
	    } catch (e:java.lang.RuntimeException) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		JClass.anError("anError");
	    } catch (e:java.lang.Error) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}

	// For @Native method, we can catch Java exception with corresponding X10 exception
	passed = false;
	try {
	    try {
		@Native("java", "JClass.throwable(\"throwable\");") {}
	    } catch (e:x10.lang.Throwable) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		@Native("java", "JClass.exception(\"exception\");") {}
	    } catch (e:x10.lang.Exception) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		@Native("java", "JClass.runtimeException(\"runtimeException\");") {}
	    } catch (e:x10.lang.RuntimeException) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = false;
	try {
	    try {
		@Native("java", "JClass.error(\"error\");") {}
	    } catch (e:x10.lang.Error) {
		passed = true;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}

	// For @Native method, we can catch Java exception with corresponding X10 exception
	passed = true;
	try {
	    try {
		@Native("java", "JClass.aThrowable(\"throwable\");") {}
	    } catch (e:x10.lang.Throwable) {
		passed = false;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = true;
	try {
	    try {
		@Native("java", "JClass.anException(\"exception\");") {}
	    } catch (e:x10.lang.Throwable) {
		passed = false;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = true;
	try {
	    try {
		@Native("java", "JClass.aRuntimeException(\"runtimeException\");") {}
	    } catch (e:x10.lang.Throwable) {
		passed = false;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}
	passed = true;
	try {
	    try {
		@Native("java", "JClass.anError(\"error\");") {}
	    } catch (e:x10.lang.Throwable) {
		passed = false;
		if (DEBUG) e.printStackTrace();
	    } finally {
		allPassed &= passed;
		assert passed;
	    }
	} catch (e2:java.lang.Throwable) {}

	if (allPassed) {
	    Console.OUT.println("Passed All tests.");
	} else {
	    Console.OUT.println("Failed some tests");
	}
    }
}
