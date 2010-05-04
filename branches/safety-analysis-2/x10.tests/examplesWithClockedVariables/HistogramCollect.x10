import x10.util.Random;
import x10.io.Console;
import clocked.Clocked;
interface Reducer[R] {
  global safe def zero():R;
  global safe def reduce(R,R):R;
}
struct SumReducer implements Reducer[Int] {
   def zero()=0;
   def reduce(a:Int,b:Int)=a+b;
}
public class Histogram {

    public static def main(args:Rail[String]!) {
	val N = 100;
	val S = 5;
	val a = Rail.make[Int](N, (i:Int)=> i);

    val b = Rail.make(SumReducer(), S);
	finish for((i) in 0..N-1)  {
			async {
	       		val bin = a(ii) % S;
	      	    b(bin) <- 1;
	    
	       }
	}
    Console.OUT.println("Test ok." + b(0));
    }
}
