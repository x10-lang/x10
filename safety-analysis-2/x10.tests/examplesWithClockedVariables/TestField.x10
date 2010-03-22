import clocked.*;

public class TestField {

	var c: Clock;
	var d: Clock;
	var x: int @ Clocked[Int] (c, op);
	val op = Int.+;
	var y: int = 0;

	def this() {
 	   c = Clock.make();
	   x = 0;	
	}

	public def run() {
			shared var z: int @ Clocked [int] (d, op) = 0;
			z = 3;
			async {
				finish  async clocked(c) { x = 3;}
				finish  async  { x = 3; z = 2;}
			}
			// atomic x = 3;
			val l = y;	
		
	
		Console.OUT.println(c);
	}

	public static def main(args: Rail[String]) {
	      new TestField().run();
	}
}
