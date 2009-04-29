/**
 * Future test.
 */
public class Future0 {
	public def run() = {
	  val x = future 47;
	  x() == 47
	}

	public static def main(var args: Rail[String]): void = {
		new Future0().run();
	}
}
