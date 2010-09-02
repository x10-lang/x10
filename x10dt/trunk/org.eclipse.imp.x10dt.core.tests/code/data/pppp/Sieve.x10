import x10.lib.stream.*;

public class Sieve {
    static def sieve(N:int) {
	finish {
	    filter(gen(2,N));
	}
    }
    static def gen(a:Int, N:Int):Sink[Int]  {
	val s = new SimpleStreamImp[Int]();
	async {
	try {
	    for (var i:Int=a; i < N; i++) 
		s.put(i);
	    s.close();
	} catch (StreamClosedException) {
}
	}
	return s.sink();
    }
    static def filter(s:Sink[Int]) {
	async {
  	  try {
		val prime = s.get();
		Console.OUT.println("Found prime " + prime);
		val out = new SimpleStreamImp[Int]();
		filter(out.sink());
		try {
		    while (true) {
			val i = s.get();
			if ((i % prime) != 0) 
			    out.put(i);
		    }
	        } finally {
	         out.close();
                }
          } catch (StreamClosedException) {
          }
	}
    }
 
    public static def main(args: Rail[String]) {
	if (args.length < 1) {
	    Console.OUT.println("Usage: Sieve <N>");
	    return;
	}
	sieve(Int.parseInt(args(0)));	
    }
}