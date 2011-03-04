/**
 * An implementation of allReduce for a uniquely distributed array.
 * Uses a helper array of the same distribution.
 * 
 * This program assumes the operation to be performed is associative and 
 * commutative.
 * 
 * The program is safe.
 * 
 * @author vj
 */
public class AllReduceClock {
  static def even ( p:Int):Boolean = p % 2 == 0;
  public static def allReduce(red:DistArray[int](1), black:DistArray[int](1)) {
    val P = Place.MAX_PLACES;
    val phases = Utils.log2(P);
    clocked finish  {
    	for (p in red.dist.places()) clocked async at(p) {
    		var shift_:Int=1;
    		for ([phase] in 0..phases-1) {
    			val e = even(phase);
    			val destId = (p.id+shift_)% P;
    			if (e) {
    				val elem = black(p.id);
    				at(Place.place(destId)) 
        					red(destId) = elem + black(destId);
    			} else {
    				val elem = red(p.id);
    				at (Place.place(destId)) {
    					black(destId) = elem + red(destId);
    				}
    			}
    			shift_ *=2;
    			next;
    		}
    	}
    }
    return (even(phases-1)) ? red(0) : black(0);
  }
  public static def main(Array[String](1)) {
    assert Utils.powerOf2(Place.MAX_PLACES)
        : " Must run on power of 2 places.";
    val D = Dist.makeUnique();
    val black:DistArray[int](1) = DistArray.make[int](D, (p:Point)=> p(0));
    val red:DistArray[int](1) = DistArray.make[int](D, (Point)=> 0);
    val result = allReduce(red, black);
    Console.OUT.println("allReduce = " + result);
  }
}