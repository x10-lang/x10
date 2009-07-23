import x10.util.Random;
import x10.io.Console;

public class MontyPi {
    public static @fun def main(s: Rail[String]) {
	assert s.length >= 1 : "Usage: MontyPi <number of points>";
	val N = int.parseInt(s(0));
	val initializer = (i:Point) => @fun {
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
 