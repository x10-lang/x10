public class Test001 {
	// static operator (x: Long) ~ (y: Long) {
	// 	return "Binary";
	// }

	static operator ~ (y: Long) {
		return "Unary";
	}

	public static def main(Rail[String]) {
		Console.OUT.println("Test");
		val x = operator ~ (1);
		// val y = operator ~ (1, 1);
		Console.OUT.println("x = "+x);
		// Console.OUT.println("y = "+y);

	}
}