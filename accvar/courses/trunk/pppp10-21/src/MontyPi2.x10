	import x10.util.Random;
	
	public class MontyPi2 {
	    public static def main(s: Array[String](1)) {
	        assert s.size >= 1 : "Usage: MontyPi [<number of points per place:Int>]";
	        val N = s.size > 0 ? int.parseInt(s(0)) : 10000;
	        var start:double = - System.nanoTime();
	        val result = DistArray.make[Double](Dist.makeUnique(), (Point)=>0.0D);
	        finish  {
	            for(p in result.dist.places()) 
	                async at(p)
	            {
	                val r = new Random();
	                var a:double=0.0D;
	                for(j in 1..N) {
	                    val x = r.nextDouble(), y=r.nextDouble();
	                    if (x*x +y*y <= 1.0) a++;
	                }
	                result(p.id)=a;
	            }
	        }
	        val pi = 4*result.reduce(Double.+,0)/(N*Place.MAX_PLACES);
	        start += System.nanoTime();
	        Console.OUT.println("The value of pi is " + pi + " (t=" + (start/(1000*1000)) + " ms).");
	    }
	}
	
