import x10.util.Random;
import x10.io.Console;

public class MontyPi {
    public static  def main(s: Rail[String]) {
	if (s.length != 1) {
	    Console.OUT.println("Usage: MontyPi <number of points>");
	    System.exit(-1);
        }
	val N = Int.parseInt(s(0));
	val initializer = (i:Point) => {
	    val r = new Random();
	    var result:double=0.0D;
	    for(j in 1..N) {
		val x = r.nextDouble(), y=r.nextDouble();
		if (x*x +y*y <= 1.0) result++;
	    }
	    result
	};
	val result = Array.make[Double](Dist.makeUnique(), initializer);
	val pi = 4*result.reduce(Double.+,0)/(N*Place.MAX_PLACES);
	Console.OUT.println("The value of pi is " + pi);
    }
}
