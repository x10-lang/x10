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
public class AllReduce {
  static def even(p:int):Boolean = p % 2 == 0;
  public static def allReduce(red:Array[int](1), black:Array[int](1)) {
    val P = Place.MAX_PLACES;
    val phases = Utils.log2(P);
    var shift_:Int=1;
    for ((phase) in 0..phases-1) {
      val shift = shift_;
      finish ateach ((p) in red.dist) {
        val ev = even(phase);
        val destId = (p+shift)% P;
        val source = here;
        val elem = ev ? black(p) : red(p);
        at(Place.places(destId)) {
          if (ev) 
            red(destId) = elem + black(destId);
          else
            black(destId) = elem + red(destId);
        }
      }
      shift_ *=2;
    }
    return (even(phases-1)) ? red(0) : black(0);
  }
  public static def main(Rail[String]) {
    assert Utils.powerOf2(Place.MAX_PLACES)
        : " Must run on power of 2 places.";
    val D = Dist.makeUnique();
    val black:Array[int](1) = Array.make[int](D, (p:Point)=> p(0));
    val red:Array[int](1) = Array.make[int](D, (Point)=> 0);
    val result = allReduce(red, black);
    Console.OUT.println("allReduce = " + result);
  }
}