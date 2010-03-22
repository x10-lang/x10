import clocked.Clocked;

public class AllReduce {

  public static def allReduce(c: Clock, op: (Int,Int)=>Int, myA:Array[int @ Clocked[Int](c,op)](1)) {
    val P = Place.MAX_PLACES;
    val phases = Math.log2(P);

    
  
      finish  ateach ((p) in myA.dist)  {
        var shift_:Int=1;
        for ((phase) in 0..phases-1) {
                val destId = (p+shift_)% P;
                val source = here;
                val elem = myA(p);
                myA(destId) = elem + myA(destId);
                next;
                shift_ *=2;
        }
     }
   
   return myA(0);
  }
  public static def main(Rail[String]) {
    assert Math.powerOf2(Place.MAX_PLACES)
        : " Must run on power of 2 places.";
    val D = Dist.makeUnique();
    val c = Clock.make();
    val op = Int.+;
    val a:Array[int @ Clocked[int](c, op)](1) = Array.make[int](D, (p:Point)=> p(0));
    val result = allReduce(c, op, a);
    Console.OUT.println("allReduce = " + result);
  }
}
