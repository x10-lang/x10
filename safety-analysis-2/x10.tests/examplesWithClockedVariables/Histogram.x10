import x10.util.Random;
import x10.io.Console;
import clocked.Clocked;

public class Histogram {

    public static def main(args:Rail[String]!) {
	val N = 100;
	val S = 5;
	val c = Clock.make();
	val a = Rail.make[Int](N, (i:Int)=> i);
	val op = Int.+;

    val b = Rail.make[Int @  Clocked[Int] (c,op, 0)](S);
    var i: int = 0 ;
	finish for(i = 0; i< N; i++)  {
			val ii = i;
			async clocked(c)  {
	       		val bin = a(ii) % S;
	      	    b(bin) = 1;
	    
	       }
	}
	next;
    Console.OUT.println("Test ok." + b(0));
    }
}
