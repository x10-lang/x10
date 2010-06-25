import x10.lib.stream.*;

/** Compute the Hamming sequence, defined by:
    h = 1.omerge(2*h, omerge(3*h,5*h))
    @author vj
*/
class Hamming {

    /**
     * Output the ordered merge of the input streams.
     */
    static def omerge(a:Sink[Int], b:Sink[Int]):Sink[Int] {
        val s = new SimpleStreamImp[Int]("o(" + a + "," + b+")");
        omerge(a, b, s);
        return s.sink();
    }
    static def omerge(a:Sink[Int], b:Sink[Int], s:Source[Int]) {
        async {
            try {
                var av:Int=a.get(), bv:Int=b.get();
                while (true) {
                    if (av < bv) {
                        s.put(av);
                        av = a.get();
                    } else {
                        s.put(bv);
                        if (av == bv) // pop this to avoid outputting duplicates
                            av = a.get();
                        bv = b.get();
                    } 
                }
            } catch (StreamClosedException) {
                s.close();
            }
        }
    }
    /**
     * Need to replicate because a stream is single-writer, single-reader.
     */
    static def copy(a:Sink[Int], ox:ValRail[Stream[Int]]) {
        copy(-1, a, ox);
    }
    static def copy(n:int, a:Sink[Int], s:Stream[Int]) {
        copy(n, a, Rail.makeVal[Stream[Int]](1, (Nat)=>s));
    }
    static def copy(n:int, a:Sink[Int], ox:ValRail[Stream[Int]]) {
        async {
            var count:Int=n;
            try {
                while (--count != 0) {
                    val av = a.get();
                    for (o in ox)
                        o.put(av);
                }
            } catch (StreamClosedException) {
            } finally {
                for (o in ox)
                    o.close();
            }
        }
    }
    static def kmult(k:Int, a:Sink[Int]):Sink[Int] {
        val s = new SimpleStreamImp[Int](k + "*");
        async {
            try {
                while (true) 
                    s.put(k*a.get());
            } catch (StreamClosedException) {
                s.close();
            }
        }
        return s.sink();
        
    }
    static def hamming( n:Int):Sink[Int] {
        val s = new SimpleStreamImp[Int]("h");
        val hx = Rail.makeVal[Stream[Int]](4, (i:Nat)=>new SimpleStreamImp[Int]("h" + i) as Stream[Int]);
        async {
            try {
                copy(n, s.sink(),  hx);
                s.put(1);
                omerge(kmult(2, hx(0).sink()), 
                       omerge(kmult(3,hx(1).sink()), kmult(5,hx(2).sink())),
                       s.source());
            } catch (StreamClosedException) {
                s.close();
            }
        }
        return hx(3).sink();
    }
    static def print(s:Sink[Int]) { 
        async try {
            while (true) {
                Console.ERR.print("print " + s.get()+"\n");
            }
        } catch (StreamClosedException){
        }
    }

    public static def main(args:Rail[String]) {
        if (args.length < 1) {
            Console.ERR.print("Usage: Hamming <N>\n");
            return;
        }
        finish print(hamming(Int.parseInt(args(0))));
    }
}