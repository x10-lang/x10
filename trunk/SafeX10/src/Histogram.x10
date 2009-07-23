import x10.util.Random;
import x10.io.Console;

public class Histogram {
    public incomplete static def all[T](x:Array[T]):Set[Object];
    public incomplete static def all[T](x:Rail[T]):Set[Object];
    public incomplete static def loc[T](x:T):Set[T];
    public incomplete static def read[T](x:Set[T]):Effects;
    public incomplete static def write[T](x:Set[T]):Effects;
    public incomplete static def touch[T](x:Set[T]):Effects;
    public incomplete static def atomicInc[T](x:Set[T]):Effects ;
    public incomplete static def atomicDec[T](x:Set[T]):Effects;

    public static  @fun(read(all(a)).and(write(all(b))))
    def run(a:Array[Int], b: Rail[int]) {
	    finish 
		for ((i):Point(1) in a) async {
		val bin = a(i) % b.length;
		atomic b(bin)++;
	    
	}
    }
    public static @fun def main(args:Rail[String]) {
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
