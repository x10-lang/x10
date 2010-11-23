public class PrefixOrig {
    const N = 64;
    global val a = Rail.make[int](N, (i:Int)=> i);

    public def run() = run(0, N-1);
    public def run(lo:int, hi:int) {
        if (lo+1 == hi) {
       		val e = a(lo);
       		a(hi)= e + a(hi);
        	return;
        }
        val mid = lo + ((hi-lo+1)/2);
        finish {
          async run(lo, mid-1);
          run(mid, hi);
        }
        { //expand
            val e = a(mid-1);
            finish for ((p) in mid..hi)
                async
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
    	Console.ERR.print( compute_time + " ");
    }
}
