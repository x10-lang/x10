import x10.util.Random;
import x10.io.Console;
import clocked.Clocked;

public class Histogram {

    public static def main(args:Rail[String]!) {
	val N = 100;
	val S = 5;
	val c = Clock.make();
	val a = Array.make[int](0..N-1, ((i):Point)=> i);
	val op = Int.+;
	static type cl = int @  Clocked[Int] (c,op);
    val b : Rail [int @ Clocked [int] (c, op)]! = Rail.make[int](S);
	foreach((i) in a.region) clocked (c) {
	       val bin = a(i)% S;
	       b(bin) = 1;
	    
	}
	next;
        Console.OUT.println("Test ok." + b(0));
    }
}
