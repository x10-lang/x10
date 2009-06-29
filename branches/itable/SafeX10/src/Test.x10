import x10.util.Random;
import x10.io.Console;
public class Test {
    public static def main(args:Rail[String]) {
	@fun {
	    val M = 10;
	    val b = Rail.makeVar[int](M);
	    for (i in b) 
		Console.OUT.println("i=" + i );
	    //	    val low = b.region.min(), high = b.region.max();
	    //	    Console.OUT.println("M=" + M + " b.min()=" + low + " b.max=" + high);
	    //	    assert low==0;
	    //	    assert high==M-1;
	}
    }
}
