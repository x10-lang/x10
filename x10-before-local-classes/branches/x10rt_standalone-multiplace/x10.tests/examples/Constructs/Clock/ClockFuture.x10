/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Test for the interaction of clocks and future.
 * clock.doNext should not wait for futures to
 * terminate.
 * @author Christoph von Praun
 */
public class ClockFuture extends x10Test {

    private var clock_has_advanced: boolean;

    public def m(): int = {
	var ret: int = 0;
	when (clock_has_advanced) {
	    ret = 42;
	}
	return ret;
    }

    public def run(): boolean = {
	c: Clock = Clock.make();
	var f: Future[int] = future (here) { m() };
	x10.io.Console.OUT.print("1 ... ");
	// this next should not wait on the future
	next;
	x10.io.Console.OUT.print("2 ... ");
	atomic { clock_has_advanced = true; }
	x10.io.Console.OUT.print("3 ...");
	var result: int = f.force();
	chk(result == 42);
	x10.io.Console.OUT.println("4");
	return true;
    }

    public static def main(var args: Rail[String]): void = {
	new ClockFuture().execute();
    }
}
