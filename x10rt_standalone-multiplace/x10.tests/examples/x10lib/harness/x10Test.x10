/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
package harness;

import x10.util.Random;
import x10.io.Console;


/**
 * Test harness abstract class.
 */

abstract public class x10Test {

    /**
     * The body of the test.
     * @return true on success, false on failure
     */
    abstract public def run(): boolean;

    public def executeAsync() {
        val b: Rail[boolean]! = [ false ]; // use a rail until we have shared locals working
        try {
            finish async b(0) = this.run();
        } catch (e: Throwable) {
            e.printStackTrace();
        }
        reportResult(b(0));
    }

    public def execute(): void = {
        var b: boolean = false;
        try {
            finish b = this.run();
        } catch (e: Throwable) {
            e.printStackTrace();
        }
        reportResult(b);
    }

    public const PREFIX: String = "++++++ ";

    public static def success(): void = {
        println(PREFIX+"Test succeeded.");
	x10.lang.Runtime.setExitCode(0);
    }

    public static def failure(): void = {
        println(PREFIX+"Test failed.");
        x10.lang.Runtime.setExitCode(1);
    }

    protected static def reportResult(b: boolean): void = {
        if (b) success(); else failure();
    }

    /**
     * Check if a given condition is true, and throw an error if not.
     */
    public static def chk(b: boolean): void = {
        if (!b) throw new Error();
    }

    /**
     * Check if a given condition is true, and throw an error with a given
     * message if not.
     */
    public static def chk(b: boolean, s: String): void = {
        if (!b) throw new Error(s);
    }

    private var myRand:Random! = new Random(1L);

    /**
     * Return a random integer between lb and ub (inclusive)
     */

    protected def ranInt(lb: int, ub: int): int = {
        return lb + myRand.nextInt(ub-lb+1);
    }

    protected var result: boolean;
    protected def check[T](test:String, actual:T, expected:T) = {
	result = actual == expected;
	println(test + (result ? " succeeds: got " 
			: " fails: exepected " + expected + ", got " )
		+ actual);
    }


    protected static def println(s:String) = x10.io.Console.OUT.println(s);
}
