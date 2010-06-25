/** Use Horner's rule to evaluate a polynomial on degree n. The rule is:

   y = (..(((a_n * x + a_{n-1})* x + a_{n-2})* x + a_{n-3})* x + ... + a_1)* x + a_0

   The program uses an n-stage pipeline.
    
 */
import x10.lib.stream.*;
public class Horner {

    static def evalPoly(w:ValRail[Int], ix:Sink[Int]):Sink[Int] {
	val nplus1 = w.length;
	val n = nplus1 -1;
	val zs = Rail.makeVal[Stream[Int]](n, (Nat)=> new SimpleStreamImp[Int]() as Stream[Int]);
	val xs = Rail.makeVal[Stream[Int]](n, (Nat)=> new SimpleStreamImp[Int]() as Stream[Int]);
	foreach ((i) in 0..n-1) 
	    try {
		while (true) {
		    val x = i==0? ix.get() : xs(i-1).get();
		    val a = i==0? w(n) : zs(i-1).get();
		    xs(i).put(x);
		    zs(i).put(x*a+w(n-i-1));
		}
	    } catch (StreamClosedException) {
	    } finally {
		xs(i).close();
		zs(i).close();
	    }
	return zs(n-1).sink();
    } 
    public static def main(Rail[String]) {
	val w = [1,2,3];
	val xs = new ValRailSource[Int]([1,2,3,4,5]);
	PrintSink.make(evalPoly(w, xs));
    }
}