import x10.util.Random;
public value PrefixClock {
    val P:Int;
    val a:Rail[Int];
    def this(p:Int) {
	this.P=p;
        val r = new Random(2156L);
	this.a= Rail.makeVar[Int](P, (i:Nat)=> r.nextInt(100));
    }
    public def prefixSum() {
        finish async {
	    val c = Clock.make();
	    foreach ((t) in 1..P-1) clocked(c) {
		var b:Int=t; // we will examine the rightmost bit of b in turn
                var gap:Int=0; // distance from t to the next number that is (k*2^size -1) for some k
		for (var size:Int=1; size < P; size *=2) {
                    gap += (b%2)==0 ? size : 0;
		    if (size - gap > 0)
			a(t) += a(t-(size-gap));
		    next;
		    b = b >> 1;
		}
	    }
	}
    }
    public def print(prefix:String) {
	Console.OUT.println(prefix);
	for ((p) in a.region)
             Console.OUT.print(" " + a(p));
	Console.OUT.println();
    }

    public static def main(args:Rail[String]) {
	if (args.length() < 1) {
	    Console.OUT.println("Usage: <exec> <p:Int>");
	}
        val P = Int.parseInt(args(0));
	assert Utils.powerOf2(P) : " Must run with power of 2 activities.";
	val s = new PrefixClock(P);
        s.print("Initial:");
	s.prefixSum();
	s.print("Final:");
    }
}
/*
---
  a  b   c  d
     a+b    c+d
         a+b+c a+b+c+d
  000 001   010   011    100   101   110   111
  a   b     c     d      e     f     g     h
  a 0 a:b 1 c   0 c:d 1  e   0 e:f 1 g   0 g:h 1
  a 0 a:b 0 a:c 1 a:d 2  e   0 e:f 0 e:g 1 e:h 2
  a 0 a:b 0 a:c 0 a:d 0  a:e 1 a:f 2 a:g 3 a:h 4
*/
