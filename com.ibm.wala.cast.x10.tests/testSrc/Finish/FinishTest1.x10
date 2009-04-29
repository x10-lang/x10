/**
 * Minimal test for finish, using an async.
 * run() method returns true if successful, false otherwise.
 * @author Vivek Sarkar (vsarkar@us.ibm.com)
 */
public class FinishTest1 {

	var flag: boolean;

	public def run(): boolean = {
		finish {
			async (here) { atomic { flag = true; } }
		}
		var b: boolean;
		atomic { b = flag; }
		return b;
	}

	public static def main(var args: Rail[String]): void = {
		new FinishTest1().run();
	}
}
