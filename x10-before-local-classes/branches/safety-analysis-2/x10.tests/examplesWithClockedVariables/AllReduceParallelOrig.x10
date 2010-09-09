

public class AllReduceParallelOrig {
  
  static val P = 8;

  public static def allReduce(c: Clock, op: (int,int)=>int, myA:Rail[int]!) {
    val phases = Math.log2(P);
	var i : Int = 0;
    for(i = 1; i < P; i++)  {
    	val p = i;
        async clocked(c) {
        	var shift_:Int=1;
        	for ((phase) in 0..phases-1) {
               	val destId = (p+shift_)% P;
                val source = here;
                val elem = myA(p);
                val nextVal = elem + myA(destId);
                next;
            	myA(destId) = nextVal;
                next;
                shift_ *=2;
        	}
     	}
     }
     var shift_:Int=1;
     for ((phase) in 0..phases-1) {
               	val destId = (0+shift_)% P;
                val source = here;
                val elem = myA(0);
                val nextVal = elem + myA(destId);
                next;
            	myA(destId) = nextVal;
                next;
                shift_ *=2;
        	}

     return myA(0);
}

  public static def main(Rail[String]) {

    val c = Clock.make();
    val op = int.+;
    val myA = Rail.make [int](P, (i:Int)=> i);
    val result = allReduce(c, op, myA);
   
    Console.OUT.println("allReduce = " + result);
  }
}
