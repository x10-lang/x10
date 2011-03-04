import x10.util.Random;
import x10.io.Console;


public class HistogramOrig {

    public static def main(args:Rail[String]!) {
    	val start_time = System.currentTimeMillis(); 
	val N = 64;
	val S = 100000;
	val REPEAT = 6000;
	val B = 10;

	val a = Rail.make[Int](N * S, (i:Int)=> i);


    val b = Rail.make[Int](B);
    var i: int = 0 ;
	finish for(i = 0; i< N; i++)  {
			val ii = i;
			async {
			var j: int = 0;
			var k: int = 0;
			for (k = 0; k < REPEAT; k++)
			for (j = ii * S; j < (ii + 1) * S; j++) { 
	       			val bin = a(j) % S;
				if (bin < B)
	      	    			atomic b(bin)++;
			}
	    
	       }
	}
	
    Console.OUT.println("Test ok." + b(0));
    val compute_time = (System.currentTimeMillis() - start_time);
    Console.OUT.println( compute_time + " ");
    }
}
