import clocked.*;

public class AllReduceParallel {
  
  static val P = 8;

  public static def allReduce(c: Clock, op: (int,int)=>int, myA:Rail[int @ Clocked[int](c,op, 0)]!) @ClockedM(c) {
    val phases = Math.log2(P);
	var i : Int = 0;
    for(i = 0; i < P; i++)  {
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
     next;
     return myA(0);
}

  public static def main(Rail[String]) {

    val c = Clock.make();
    val op = int.+;
    val myA = Rail.make [int @ Clocked[int](c, op, 0)](P, (i:Int)=> i);
    val result = allReduce(c, op, myA);
   
    Console.OUT.println("allReduce = " + result);
  }
}
