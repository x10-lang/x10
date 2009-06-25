/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * OLD SEMANTICS:
 * The x10 compiler must conservatively check if an activity can
 * potentially pass a clock it is not registered with, to a subactivity.
 * If so, it must report a compile time error.
 *
 * Language clarification needed on disambiguation
 * algorithm to use.
 *
 * Compile time analysis may not be possible in some cases.
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
 * @author kemal 5/2005
 */
public class ClockTest16 extends x10Test {

	public def run(): boolean = {
		val x: X = new X();
		try {
			finish async {
				val c0 = Clock.make();
				val c1 = Clock.make();
				val ca: Rail[Clock] = [c0,c1];

				// Question:
				// Can an activity ever pass a clock it is not
				// registered with to a
				// subactivity of itself, in statement async(cx) S?

				// Compiler answer: NO, actual runtime answer: NO
				async clocked(c1) {
					val cx = ca(1);
					async clocked(cx) { // no clock use error
						next;
					}
					next;
				}

				// Compiler: MAYBE, actual: NO
				async clocked(c1) {
					val cx = ca(x.one());
					async clocked(cx) { //no clock use error
						next;
					}
					next;
				}

				// Compiler: MAYBE, actual: YES
				async clocked(c1) {
					val cx = ca(x.zero());
					async clocked(cx) { // clock use error
						next;
					}
					next;
				}

				val f0: foo = new foo() {
					public def apply(): void = {
						val cx = ca(x.zero());
						async clocked(cx) { // clock use error
							next;
						}
					}
				};

				val f1: foo = new foo() {
					public def apply(): void = {
						val cx = ca(x.one());
						async clocked(cx) { // no clock use error
							next;
						}
					}
				};

				val fooArray: Rail[foo] = [f0,f1];

				// Compiler: MAYBE, Actual: NO
				Y.test(fooArray(x.one()), c1);

				// Compiler: MAYBE, Actual: YES
				Y.test(fooArray(x.zero()), c1);

				// Compiler: YES, actual: YES
				async clocked(c1) {
					val cx = ca(0);
					async clocked(cx) { // clock use error
						next;
					}
					next;
				}

				next;
			}
			return false;
		} catch (var e: ClockUseException) {
		} catch (var e: MultipleExceptions) {
		    for (var ex:Throwable in e.exceptions())
		       if (! (ex instanceof ClockUseException))
		          return false;
		}

		return true;
	}

	public static def main(var args: Rail[String]): void = {
		new ClockTest16().execute();
	}

	/**
	 * A class to invoke a 'function pointer' inside of async
	 */
	static class Y {
		static def test(val f: foo, val c: Clock): void = {
			// Compiler analysis may not be possible here
			async clocked(c) {
				f.apply(); // it is hard to determine f does an async clocked(c2) S, where c2 != c
				next;
			}
		}
	}

	/**
	 * An interface to use like a simple 'function pointer'
	 *
	 * foo f1 = new foo() { public void apply() S1 }; //assign body S1 to f1
	 *
	 * // values of free final variables of S1 are also captured in f1
	 *
	 * f1.apply(); // invoke S1 indirectly using its captured
	 *
	 * // free variables
	 */
	static interface foo {
		public def apply(): void;
	}

	/**
	 * Dummy class to make static memory disambiguation difficult
	 * for a typical compiler
	 */
	static class X {
		public val z: Rail[int] = [1,0];
		def zero() = z(z(1)); 
		def one() = z(z(z(0)));
		def modify(): void = { z(0) += 1; }
	}
}
