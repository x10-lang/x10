import x10.util.Random;
import x10.io.Console;


public class HistogramOrig {

    public static def main(args:Rail[String]!) {
    	val start_time = System.currentTimeMillis(); 
	val N = 100;
	val S = 5;

	val a = Rail.make[Int](N, (i:Int)=> i);


    val b = Rail.make[Int](S);
    var i: int = 0 ;
	finish for(i = 0; i< N; i++)  {
			val ii = i;
			async {
	       		val bin = a(ii) % S;
	      	    atomic b(bin)++;
	    
	       }
	}
	
    Console.OUT.println("Test ok." + b(0));
    val compute_time = (System.currentTimeMillis() - start_time);
    Console.ERR.print( compute_time + " ");
    }
}
