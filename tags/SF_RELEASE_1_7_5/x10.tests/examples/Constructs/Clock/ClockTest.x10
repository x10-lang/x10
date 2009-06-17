/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Minimal test for clock.  Does not do anything
 * interesting.  Only possible failure is to not
 * compile or hang.
 */
public class ClockTest extends x10Test {
    public def run(): boolean = {
	c: Clock = Clock.make();
	next;
	c.resume();
	c.drop();
	return true;
    }

    public static def main(var args: Rail[String]): void = {
	new ClockTest().execute();
    }
}
