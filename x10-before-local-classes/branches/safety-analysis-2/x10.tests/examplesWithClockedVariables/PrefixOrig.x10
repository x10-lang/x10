public class PrefixOrig {
    const N = 8;
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
        assert Math.powerOf2(Place.MAX_PLACES) : " Must run on power of 2 places.";
        val s = new PrefixOrig();
        s.run();
        s.print();
    }
}