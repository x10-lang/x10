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

package harness;

import x10.compiler.WS;
import x10.io.Console;
import x10.io.File;
import x10.util.Random;

/**
 * Test harness abstract class.
 */
abstract public class x10Test {

    /**
     * The body of the test.
     * @return true on success, false on failure
     */
    @WS abstract public def run(): boolean;

    public def executeAsync() {
        val b = new Cell[Boolean](false);  
        try {
            finish async b(this.run());
        } catch (e: CheckedThrowable) {
            e.printStackTrace();
        }
        reportResult(b());
    }

    public def execute(): void {
        var b: boolean = false;
        try {
            finish b = this.run();
        } catch (e: CheckedThrowable) {
            e.printStackTrace();
        }
        reportResult(b);
    }

    // Convert a partial path and a file name into
    // an absolute path to the file.
    // If being run under the nightly test harness, then
    // X10_TEST_DIR will be set in the environment and will be used
    // to build up a path.  
    // If X10_TEST_DIR is not set, this method simply returns file
    public static def pathCombine(prefix:String, file:String):String {
        val home = System.getenv("X10_TEST_DIR");
        if (home == null) {
            return file;
        } else {
            return home+File.SEPARATOR+prefix+File.SEPARATOR+file;
        }
    }
    public static def pathCombine(prefix:Rail[String], file:String):String {
        if (prefix.size == 0) return file;
        val sb = new x10.util.StringBuilder();
	for (i in 0..(prefix.size-2)) {
          sb.add(prefix(i));
          sb.add(File.SEPARATOR);
        }
        sb.add(prefix(prefix.size-1));
        return pathCombine(sb.toString(), file);
    }

    public static PREFIX: String = "++++++ ";

    public static def success(): void {
        println(PREFIX+"Test succeeded.");
        System.setExitCode(0n);
    }

    public static def failure(): void {
        println(PREFIX+"Test failed.");
        System.setExitCode(1n);
    }

    protected static def reportResult(b: boolean): void {
        if (b) success(); else failure();
    }

    public static class TestException extends Exception {

        public def this() { super(); }

        public def this(cause: CheckedThrowable) { super(cause); }

        public def this(message: String) { super(message); }

        public def this(message: String, cause: CheckedThrowable) { super(message, cause); }

    }

    /**
     * Check if a given condition is true, and throw an error if not.
     */
    public static def chk(b: boolean): void {
        if (!b) throw new TestException();
    }

    /**
     * Check if a given condition is true, and throw an error with a given
     * message if not.
     */
    public static def chk(b: boolean, s: String): void {
        if (!b) throw new TestException(s);
    }

    private var myRand:Random = new Random(1L);

    /**
     * Return a random integer between lb and ub (inclusive)
     */
    protected def ranInt(lb: int, ub: int): int {
        return lb + myRand.nextInt(ub-lb+1n);
    }

    protected static def println(s:String) { x10.io.Console.OUT.println(s); }
}
