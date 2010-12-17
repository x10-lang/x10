public class PrefixOrig {
     const N  = 1048576;
     val SLICE = 2048;
    global val a = Rail.make[int](N, (i:Int)=> i);

    public def run() = run(0, N-1);
    public def run(lo:int, hi:int) {
 	  if (hi - lo < SLICE) { 
		var i: int = 0;
       		var eprev: int = a(lo);
		for (i = lo + 1; i <= hi; i++) {
       			var e: int = eprev + a(i);
			a(i) = a(i) + eprev;
			eprev = e;
			
		}
        	return;
        }
        val mid = lo + ((hi-lo+1)/2);
        finish {
          async run(lo, mid-1);
          run(mid, hi);
        }
        { //expand
            val e = a(mid-1);
            for ((p) in mid..hi)
                    a(p) = e + a(p);
        }
    }
    public def print() {
        for ((p) in 0..N-1)
             Console.OUT.println("a(" + p+ ")= " + a(p));
    }

    public static def main(Rail[String]) {
    	val start_time = System.currentTimeMillis(); 
        val s = new PrefixOrig();
        s.run();
        s.print();
    	val compute_time = (System.currentTimeMillis() - start_time);
    	Console.OUT.println( compute_time + " ");
    }
}
