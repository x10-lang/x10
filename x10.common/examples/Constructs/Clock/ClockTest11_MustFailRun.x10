import harness.x10Test;

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
 * Hence this file is renamed as *MustFailRun.x10
 *
 * @author kemal 4/2005
 */
public class ClockTest11_MustFailRun extends x10Test {

	public boolean run() {
		finish async {
			final clock c = clock.factory.clock();
			final clock d = clock.factory.clock();
			async clocked(d) {
				async clocked(c) { System.out.println("hello"); }
			}
		}
		return true;
	}

	public static void main(String[] args) {
		new ClockTest11_MustFailRun().execute();
	}
}

