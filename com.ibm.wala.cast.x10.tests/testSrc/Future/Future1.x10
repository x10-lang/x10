/**
 * Future test.
 */
public class Future1 {
	public def run(): boolean = {
		val x  = future  41 ;
		return x()+1 == 42;
	}

	public static def main(var args: Rail[String]): void = {
		new Future1().run();
	}
}
