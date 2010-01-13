import x10.io.Console;

/**
 * Version of Integrate with exp hardwired to 2.
 *
 * Sample program using Guassian Quadrature for numerical integration.
 * Inspired by a 
 * <A href="http://www.cs.uga.edu/~dkl/filaments/dist.html"> Filaments</A>
 * demo program.
 * 
 */
public final class Integrate2 {

    const errorTolerance:double = 1.0e-12;

    const SERIAL:int = -1;
    const DYNAMIC:int = 0;
    const FUTURE:int = 1;
    const ASYNC:int = 2;

    const reps:int = 10;

    public static def main(args:Rail[String]!):void {
        val start:double = 0.0;
        val end:double = 1536.0; // 2048.0; // 8192.0;
	var forkPolicy:int = DYNAMIC;
	var forkArg:String = "dynamic";

        try {
            forkArg = args(0);
            if (forkArg(0) == 's')
                forkPolicy = SERIAL;
            else if (forkArg(0) == 'f')
                forkPolicy = FUTURE;
            else if (forkArg(0) == 'a')
                forkPolicy = ASYNC;
        }
        catch (e:Exception) {
            Console.OUT.println("Usage: Integrate2 <s[erial] | d[ynamic] | f[uture] | a[sync] - default d>");
            return;
        }

        Console.OUT.println("Integrating from " + start + " to " + end + 
                            " forkPolicy = " + forkArg);

        for (var i:int = 0; i < reps; ++i) {
            var a:double = 0;
	    val startTime = System.nanoTime();
            if (forkPolicy == SERIAL) {
                  a = SQuad.computeArea(start, end);
            } else if (forkPolicy == FUTURE) {
                  a = FQuad.computeArea(start, end);
            } else if (forkPolicy == ASYNC) {
                  a = AQuad.computeArea(start, end);
            } else {
                  a = DQuad.computeArea(start, end);
            }
            val now = System.nanoTime();
            val s = ((now - startTime) as double)/1e9;
            Console.OUT.println("Time: "+ s);
            Console.OUT.print(" Area: " + a);
            Console.OUT.println();
        }
    }

    static def computeFunction(x:double):double  {
        return (x * x + 1.0) * x;
    }

    static final class SQuad {
        static def computeArea(l:double, r:double):double {
            val q = new SQuad(l, r, 0);
	    q.compute();
            return q.area;
        }

        val left:double;       // lower bound
        val right:double;      // upper bound
        var area:double;
        
        def this(l:double, r:double, a:double) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final def compute():void {
            val l = left;
            val r = right;
            area = recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }
        
        // fully recursive version
        static def recEval(l:double, r:double, fl:double,
                           fr:double, a:double) {
            val h:double = (r - l) * 0.5;
            val c:double = l + h;
            val fc:double = (c * c + 1.0) * c; 
            val hh:double = h * 0.5;
            val al:double = (fl + fc) * hh; 
            val ar:double = (fr + fc) * hh;
            val alr:double = al + ar;
            var diff:double = alr - a;
	    if (diff < 0) diff = -diff;
            if (diff <= errorTolerance) {
                return alr;
            } else {
                return recEval(c, r, fc, fr, ar) + recEval(l, c, fl, fc, al);
            }
        }

    }

    //....................................

    static final class FQuad {
        static def computeArea(l:double, r:double):double {
            val q = new FQuad(l, r, 0);
            q.compute();
            return q.area;
        }

        val left:double;       // lower bound
        val right:double;      // upper bound
        var area:double;
        
        def this(l:double, r:double, a:double) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final def compute() {
            val l = left;
            val r = right;
            area = recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }
        
        // fully recursive version
        static def recEval(l:double, r:double, fl:double,
                           fr:double, a:double):double {
            val h:double = (r - l) * 0.5;
            val c:double = l + h;
            val fc:double = (c * c + 1.0) * c; 
            val hh:double = h * 0.5;
            val al:double = (fl + fc) * hh; 
            val ar:double = (fr + fc) * hh;
            val alr:double = al + ar;
            var diff:double = alr - a;
	    if (diff < 0) diff = -diff;
            if (diff <= errorTolerance)
                return alr;
	    else {
		val expr1 = future (here) recEval(c, r, fc, fr, ar);
		val expr2 = recEval(l, c, fl, fc, al);
		return expr1() + expr2;
	   }
        }
    }

    //....................................

    static final class AQuad {
        static def computeArea(l:double, r:double):double {
            val q = new AQuad(l, r, 0);
            q.compute();
            return q.area;
        }

        val left:double;       // lower bound
        val right:double;      // upper bound
        var area:double;
        
        def this(l:double, r:double, a:double) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final def compute() {
            val l = left;
            val r = right;
            area = recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }

        static final class resHolder {
	    var value:double;
        }
        
        // fully recursive version
        static def recEval(l:double, r:double, fl:double,
                           fr:double, a:double):double {
            val h:double = (r - l) * 0.5;
            val c:double = l + h;
            val fc:double = (c * c + 1.0) * c; 
            val hh:double = h * 0.5;
            val al:double = (fl + fc) * hh; 
            val ar:double = (fr + fc) * hh;
            val alr:double = al + ar;
            var diff:double = alr - a;
	    if (diff < 0) diff = -diff;
            if (diff <= errorTolerance)
                return alr;
	    else {
	        val resHolder = new resHolder();
                var expr2:double = 0;
	        finish {
		    async { resHolder.value = recEval(c, r, fc, fr, ar); };
	            expr2 = recEval(l, c, fl, fc, al);
                }
		return resHolder.value + expr2;
	   }
        }
    }

    // ...........................

    static final class DQuad {
        static def computeArea(l:double, r:double):double {
            val q = new DQuad(l, r, 0);
            q.compute();
            return q.area;
        }

        val left:double;       // lower bound
        val right:double;      // upper bound
        var area:double;
        
        def this(l:double, r:double, a:double) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final def compute():void {
            val l:double = left;
            val r:double = right;
            area = recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }
        
        // fully recursive version
        static def recEval(l:double, r:double, fl:double, fr:double, a:double):double {
            val h:double = (r - l) * 0.5;
            val c:double = l + h;
            val fc:double = (c * c + 1.0) * c; 
            val hh:double = h * 0.5;
            val al:double = (fl + fc) * hh; 
            val ar:double = (fr + fc) * hh;
            val alr:double = al + ar;
            var diff:double = alr - a;
	    if (diff < 0) diff = -diff;
            if (diff <= errorTolerance) {
                return alr;
	    } else {
	        if (x10.lang.Runtime.surplusActivityCount() <= 3) {
		    val expr1 = future (here) recEval(c, r, fc, fr, ar);
		    val expr2 = recEval(l, c, fl, fc, al);
		    return expr1() + expr2;
	        } else {
	            return recEval(c, r, fc, fr, ar) + recEval(l, c, fl, fc, al);
                }
            }
        }

    }
}

  
