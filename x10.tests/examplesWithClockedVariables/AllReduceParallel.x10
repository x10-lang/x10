import clocked.Clocked;

public class AllReduceParallel {
  
  static val P = Place.MAX_PLACES;

  public static def allReduce(c: Clock, op: (int,int)=>int, myA:Rail[int @ Clocked[int](c,op)]!) {
    val phases = Math.log2(P);
	var i : Int = 0;
    finish  for(i = 0; i < P; i++)  {
    	val p = i;
        async clocked(c) {
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
     }
     return myA(0);
}

  public static def main(Rail[String]) {
    assert Math.powerOf2(Place.MAX_PLACES)
        : " Must run on power of 2 places.";
  
    val c = Clock.make();
    val op = int.+;
    val myA : Rail[int @ Clocked[int](c, op)]! = Rail.make[int](P, (Int)=> 0);
    val result = allReduce(c, op, myA);
   
    Console.OUT.println("allReduce = " + result);
  }
}
