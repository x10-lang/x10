//OPTIONS: -PLUGINS=x10.klock.plugin.KlockPlugin
//CLASSPATH: ../classes/klock.jar
/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */


/**
 * Tests if we can assign a clock to an array element or
 * field.
 * Tests if clocks can be "aliased" per x10 manual terminology.
 * E.g.: clock c1 = clock.factory.clock(); clock c2 = c1;
 * //clocks c1 and c2 are "aliased"
 * ca[0] = c1; ca[1] = ca[0];
 * // ca[0] and ca[1] are "aliased"
 * The language definition needs to be cleared up, to define the behavior of this test.
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
 * @author kemal 4/2005
 */
public class ClockTest8_MustFailRun  {

	public boolean run() {
		finish async {
			BoxedClock bc = new BoxedClock(clock.factory.clock());
			clock[] ca = new clock[] { clock.factory.clock(), bc.val };
			final clock c1 = ca[1];
			final clock c2 = c1; //aliased clocks c2 and c1
			final clock c3 = ca[0];
			bc.val.drop();
			//TODO: the following line (arrays of clocks) does not parse

			//async clocked(ca[U.zero()])
			final clock c4 = ca[U.zero()];
			async clocked(c4) {
				async clocked(c2) { System.out.println("hello"); }
			}
		}
		return true;
	}

	public static void main(String[] args) {
		new ClockTest8_MustFailRun().run();
	}

	static class BoxedClock {
		public clock val;
		public BoxedClock(final clock x) {
			val = x;
		}
	}

	static class U {
		public static int zero() { return 0; }
	}
}

