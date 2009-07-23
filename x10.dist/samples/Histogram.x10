import x10.util.Random;
import x10.io.Console;

public class Histogram {
    /**
      * Compute the histogram of the array a in the rail b.
    */
    public static  def run(a:Array[int](1), b: Rail[int]) {
	finish 
	    foreach((i) in a.region) {
	       val bin = a(i)% b.length;
	       atomic b(bin)++;
	    }
    }
    public static def main(args:Rail[String]) {
	if (args.length != 2) {
	    Console.OUT.println("Usage: Histogram SizeOfArray Buckets");
	    System.exit(-1);
        }
	val N = int.parseInt(args(0));
	val S=int.parseInt(args(1));
	val a = Array.make[int](0..N-1, ((i):Point)=> i);
	val b = Rail.makeVar[int](S);
	run(a, b);
	val v = b(0);
        var ok:boolean = true;
	for (x in b) ok &= (x==v);
	if (ok) {
	    Console.OUT.println("Test ok.");
	} else {
	    Console.OUT.println("Test failed.");
	}
    }
}