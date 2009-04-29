/**
 * Testing the ability to assign to the field of an object
 * at place here a reference to an object at place here.next().
 *
 * @author vj
 */
public class AsyncNext {

	public def run(): boolean = {
		val Other: Place = here.next();
		val t: T = new T();
		finish async (Other) {
			val t1: T = new T();
			async (t.location) t.val = t1;
		}
		return t.val.location == Other;
	}

	public static def main(var args: Rail[String]): void = {
		new AsyncNext().run();
	}

	static class T {
		var val: Ref;
	}
}
