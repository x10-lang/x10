/**
 * atomic enclosing a void function call
 * that throws an exception.
 *
 * @author kemal 4/2005
 */
public class Atomic2 {

	var x: int = 0;


	public def run(): boolean = {
		finish async(this.location) atomic x++;
		atomic chk(x == 1);

		var gotException: boolean = false;
		try {
			atomic chk(x == 0);
		} catch (var e: Throwable) {
			gotException = true;
		}
		chk(gotException);
		return true;
	}

	public static def chk(b: boolean, s: String): void = {
        if (!b) throw new Error(s);
    }

	public static def main(var args: Rail[String]): void = {
		new Atomic2().run();
	}
}
