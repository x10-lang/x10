/**
 * Checks if finish also consideres grand-children.
 * @author Christoph von Praun
 */
public class FinishTest2 {

	var flag: boolean;
	var foo: int;

	public def run(): boolean = {
		atomic flag = false;
		finish {
			async (here) {
				atomic foo = 123;
				async (here) {
					atomic foo = 42;
					x10.io.Console.OUT.print("waiting ...");
					x10.lang.Runtime.sleep(2000);
					x10.io.Console.OUT.println("done.");
					atomic flag = true;
				}
			}
		}
		var b: boolean;
		atomic b = flag;
		x10.io.Console.OUT.println("The flag is b = " + b + " (should be true).");
		return (b == true);
	}

	public static def main(var args: Rail[String]): void = {
		new FinishTest2().run();
	}
}
