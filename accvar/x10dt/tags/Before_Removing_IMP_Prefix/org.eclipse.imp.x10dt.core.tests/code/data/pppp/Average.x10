import x10.lib.stream.*;

public class Average {
    static def average(N:int, k:Int) {
	finish 
            average(gen(1,N), k);
    }
    static def gen(low:Int, high:Int):Sink[Int]  {
	val s = new SimpleStreamImp[Int]();
	async {
	    try {
		val rng = new x10.util.Random();
		for ((i) in low..high)
		    s.put(rng.nextInt(10));
		s.close();
	    } catch (StreamClosedException) {
	    }
	}
	return s.sink();
    }
    static def average(ix:Sink[Int], k:int) {
	val s = new SimpleStreamImp[Int]();
	async {
	    val r = Rail.makeVar[Int](k, (Nat)=>0);
	    try {
		var sum:Int=0;
		for ((i) in 0..k-1) {
		    r(i)=ix.get();
		    Console.ERR.println("input " + r(i));
		    sum += r(i);
		}
		while (true) {
		    val out= sum;
		    s.put(sum);
		    sum -= r(0);
		    for ((i) in 0..k-2) // slide it down.
			r(i)=r(i+1);
		    r(k-1) = ix.get();
		    Console.ERR.println("output " + (out) + " input " + r(k-1));
		    sum += r(k-1);
		}
	    } catch (StreamClosedException) {
	    } finally {
		s.close();
	    }
	}
	return s.sink();
    }
    public static def main(args: Rail[String]) {
	if (args.length < 1) {
	    Console.OUT.println("Usage: Average <N> <k>");
	    return;
	}
	average(Int.parseInt(args(0)), Int.parseInt(args(1)));	
    }
}