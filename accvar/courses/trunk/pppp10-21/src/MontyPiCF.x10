import x10.util.Random;
import x10.io.Console;

public class MontyPiCF {
    public static  def main(s: Array[String](1)) {
    	assert s.size >= 1 : "Usage: MontyPi [<number of points per place:Int>]";
    	val N = s.size > 0 ? int.parseInt(s(0)) : 10000;
    	var start:double = - System.nanoTime();
    	val reducer = new Reducible[Double]() {
    		public def zero()=0.0D;
    		public def apply(a:Double, b:Double)=a+b;
    	};
    	val result = finish(reducer) {
    		for (p in Dist.makeUnique().places()) async at (p) {
    			val r = new Random();
        		var result:double=0.0D;
        		for(j in 1..N) {
        			val x = r.nextDouble(), y=r.nextDouble();
        			if (x*x +y*y <= 1.0) result++;
        		}
        		offer result;
    		}
    	};
    	val pi = 4*result/(N*Place.MAX_PLACES);
    	start += System.nanoTime();
        Console.OUT.println("The value of pi is " + pi + " (t=" + (start/(1000*1000)) + " ms).");
    }
}
