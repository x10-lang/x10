import x10.util.Random;
import x10.io.Console;

public class Histogram {
    /**
      * Compute the histogram of the array a in the rail b.
    */
    public static  def run(a:Array[int], b: Rail[int]) {
	finish 
	    foreach((i) in a.region) {
	       val bin = a(i)% b.length;
	       atomic b(bin)++;
	    }
    }
    public static def main(args:Rail[String]) {
	assert args.length == 2 : "Usage: Histogram SizeOfArray Buckets";
	val N = int.parseInt(args(0));
	val S=int.parseInt(args(1));
	val a = Array.make[int](0..N-1, ((i):Point)=> i);
	val b = Rail.makeVar[int](S);
	run(a, b);
	val v = b(0);
	for (x in b) assert x==v;
	Console.OUT.println("Test ok.");
    }
}