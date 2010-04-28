import clocked.*;

public class TestField {

	val c: Clock = Clock.make();
    val op = Int.+;
	var x: int @ Clocked[Int] (c, op, 0) = 0;
	var y: int = 0;


	public def run() @ ClockedM(c) {
				val z: Rail[int]! = Rail.make[int](2);
				async clocked(c) { x = 3; z(1) = 3;}
				z(1) = 2;
		
	}

	public static def main(args: Rail[String]) {
	      new TestField().run();
	}
}
