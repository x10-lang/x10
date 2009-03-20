import x10.util.Random;
import x10.io.Console;

public class Histogram {
    public  incomplete static def all[T](x:Array[T]):Set[Object];
    public  incomplete static def all[T](x:Rail[T]):Set[Object];
    public  incomplete static def loc[T](x:T):Set[T];
    public  incomplete static def read[T](x:Set[T]):Effects;
    public  incomplete static def write[T](x:Set[T]):Effects;
    public  incomplete static def touch[T](x:Set[T]):Effects;
    public  incomplete static def atomicInc[T](x:Set[T]):Effects ;
    public  incomplete static def atomicDec[T](x:Set[T]):Effects;

    public static  def run(a:Array[int], b: Rail[int]) {
	@fun(touch(all(b)).and(read(all(a))))
	    finish 
	    @parfun(atomicInc(all(b)).and(read(all(a)))) {
	    foreach((i) in a.region) 
		@fun(atomicInc(all(b)).and(read(loc(a(i))))) {
		val bin:int(0..b.length-1) = a(i)% b.length;
		atomic b(bin)++;
	    }
	}
    }
    public static def main(args:Rail[String]) {
	@fun {
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
}
