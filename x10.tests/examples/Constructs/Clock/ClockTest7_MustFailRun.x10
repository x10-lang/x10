/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Combination of finish and clocks.
 * Finish cannot pass any clock to a subactivity.
 * TODO: rename to ClockTest7 and check for exception
 *
 * OLD SEMANTICS:
 * Language clarification needed:
 * (what if async clocked(c) S occurs inside a library method
 * or is invoked via an indirect function?
 * Compiler analysis may be difficult).
 * This test currently causes a deadlock at run time.
 * How should it behave in the ultimate x10 definition?
 *
 * Temporarily declaring that this is an error that should be
 * caught at compile time (this may not be possible).
 *
 * NEW SEMANTICS: Clock Use Exception such as
 *
 *  'Transmission of c (to a child) requires that I am registered with c'
 *  'Transmission of c requires that I am not between c.resume() and a next'
 *  'The immediate body of finish  can never transmit any clocks'
 *
 * are now caught at run time. The compiler
 * can remove the run time checks using static techniques,
 * and can issue warnings when it is statically detected that
 * clock use exceptions will
 * definitely occur, or will likely occur.
 *
 * Hence this file is renamed as *MustFailRun.x10
 *
 * @author kemal 3/2005
 */
public class ClockTest7_MustFailRun extends x10Test {

	var val: int = 0;
	const N: int = 16;

	public def run(): boolean = {
		val c: Clock = Clock.make();

		finish foreach (val (i): Point in 0..(N-1)) {
			atomic val++;
			x10.io.Console.OUT.println("Activity "+i+" phase 0");
			next;
			atomic chk(val == N);
			x10.io.Console.OUT.println("Activity "+i+" phase 1");
			next;
			atomic val++;
			x10.io.Console.OUT.println("Activity "+i+" phase 2");
			c.next();
		}

		next; next; next;

		atomic chk(val == 2*N);

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest7_MustFailRun().executeAsync();
	}
}
