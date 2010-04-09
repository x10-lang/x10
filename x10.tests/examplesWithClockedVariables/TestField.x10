import clocked.*;

public class TestField {

	val c: Clock = Clock.make();
    val op = Int.+;
	var x: int @ Clocked[Int] (c, op, 0) = 0;



	public def run() @ ClockedM(c) {
	
				finish  async clocked(c) { x = 3; val z = x;}
		
	}

	public static def main(args: Rail[String]) {
	      new TestField().run();
	}
}
