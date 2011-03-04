public class Histogram {
	
	public static def run(a:Array[Int](1), b:Array[Int](1)) {
		finish 
		for ([i] in a) async {
			val bin = a(i) % b.size;
			atomic b(bin)++;
		}
	}
	public static def main(args:Array[String](1)) {
		assert args.size==2 : "Usage: Histogram <M:Int> <N:Int>";
		val M=Int.parseInt(args(0)), N = Int.parseInt(args(1));
		assert M%N == 0 : "Usage: M must be a multiple of N.";
		val a = new Array[Int](0..M-1, (q:Point) => q(0));
		val b = new Array[Int](N, 0);
		run (a,b);
		val v = b(0);
		for (x in b.values()) assert x==v;
		Console.OUT.println("Test ok.");
	}
}