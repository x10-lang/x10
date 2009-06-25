/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;;

/**
 * Cannot register a child with a clock
 * I am not registered with.
 *
 * NEW SEMANTICS: Clock Use Exception such as
 *
 * 'Transmission of c (to a child) requires that I am registered with c'
 *
 * 'Transmission of c requires that I am not between c.resume() and a next'
 *
 * 'The immediate body of finish  can never transmit any clocks'
 *
 * are now caught at run time. The compiler
 * can remove the run time checks using static techniques,
 * and can issue warnings when it is statically detected that
 * clock use exceptions will
 * definitely occur, or will likely occur.
 *
 * @author kemal 4/2005
 */
public class ClockTest11 extends x10Test {

	public def run(): boolean = {
		try {
		    finish async {
			val c = new Clock();
			val d = new Clock();
			async clocked(d) {
			    async clocked(c) { System.out.println("hello"); }
			}
		    }
		    return false;
		} catch (var e: MultipleExceptions) {
		    // Expecting only one exception
		    return false;
		} catch (var e: ClockUseException) {
		}
		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest11().execute();
	}
}
