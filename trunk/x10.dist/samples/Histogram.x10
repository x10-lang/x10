import x10.util.Random;
import x10.io.Console;

public class Histogram {
    public static  def run(a:Array[int], b: Rail[int]) {
	finish 
	    foreach((i) in a.region) {
	    val bin:int(0..b.length-1) = a(i)% b.length;
	    atomic b(bin)++;
	}
    }
    public static def main(args:Rail[String]) {
	assert args.length == 2 : "Usage: Histogram N M";
	val N = int.parseInt(args(0)), M=int.parseInt(args(1));
	assert N%M==0 : "Usage: N must be a multiple of M";
	val a = Array.make[int](0..N-1, (q:Point)=> q(0));
	val b = Rail.makeVar[int](M);
	run(a, b);
	val v = b(0);
	for (x in b) assert x==v;
	Console.OUT.println("Test ok.");
    }
}
