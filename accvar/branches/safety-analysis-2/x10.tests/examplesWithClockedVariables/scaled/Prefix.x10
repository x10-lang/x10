import clocked.*;

public class Prefix {
     const N = 1048576;
     val SLICE = 2048;
    val c = Clock.make();
    static val op = Math.noOp.(Int, Int);
    global val a = Rail.make[int @ Clocked[Int](c, op, 0)](N, (i:Int)=> i);

   
    public def run(lo:int, hi:int) @ClockedM (c) {
        if (hi - lo < SLICE) { 
		var i: int = 0;
       		var eprev: int = 0;
		for (i = lo; i <= hi; i++) {
       			var e: int = eprev + a(i);
			a(i) = a(i) + eprev;
			eprev = e;
			
		}
        	return;
        }
        finish {
        val mid = lo + ((hi-lo+1)/2);
         async clocked(c) run(lo, mid-1);
         run(mid, hi);
	 next;
        { //expand
            val e = a(mid-1);
           for ((p) in mid..hi)
                    a(p) = e + a(p);
	   next;
        }
       }
    }
    public def print() @ ClockedM(c) {
        for ((p) in 0..N-1)
             Console.OUT.println("a(" + p+ ")= " + a(p));
    }

    public static def main(Rail[String]) {
    	val start_time = System.currentTimeMillis(); 
 
        val s = new Prefix();
        s.run(0, N-1);
	next;
        s.print();
    	val compute_time = (System.currentTimeMillis() - start_time);
    	Console.OUT.println( compute_time + " ");
    }
}
