/**
 * Compute the parallel prefix of a uniquely distributed array.
 * The program can be easily adapted to compute parallel prefix for a block-distributed array.
 * 
 * This program is safe.
 * @author vj
 * 
 */
public class Prefix {
    public def run() = run(0, a.region.max(0));
    public def run(lo:int, hi:int) {
    	if (lo+1 == hi) {
    		at (a.dist(lo)) {
    			val e = a(lo);
    			at (a.dist(hi))
    			   a(hi)= e + a(hi); // preserve order
    		}
    		return;
    	}
    	val mid = lo + ((hi-lo+1)/2);
    	finish {
    		async run(lo, mid-1);
    		run(mid, hi);
    	}
    	at (a.dist(mid-1)) { //expand
    		val e = a(mid-1);
    		finish for ((p) in mid..hi) async
    			at(a.dist(p))
    		      a(p) = e + a(p);
    	}
    }
    /**
     * Print out the elements of the distributed array sequentially.
     */
    public def print() {
    	for ((p) in a.region)
    		at (a.dist(p)) 
    		  Console.OUT.println("a(" + p+ ")= " + a(p));
    }

    public static def main(Rail[String]) {
    	assert Utils.powerOf2(Place.MAX_PLACES) : " Must run on power of 2 places.";
    	val s = new Prefix();
    	s.run();
    	s.print();
    }
}