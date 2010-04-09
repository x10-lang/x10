import x10.util.Random;
import x10.io.Console;



public class MontyPiParallelOrig {

  public static  def main(s: Rail[String]) {

    val P = 4;
    val N = 10000; 
 
    val op = Double.+;
    val result = Rail.make[Double](1, (int)=>0.0D);

    finish  {
   	 var i : Int = 0;
        for(i = 0; i < P; i++) {

        async {
          
          val r = new Random();
          var a:double=0.0D;
          for(j in 1..N) {
                  val x = r.nextDouble(), y=r.nextDouble();
                  if (x*x +y*y <= 1.0) a++;
          }
          atomic result(0) += a;
        }
      }
    }  
    next;
    val pi = 4*result(0)/(N*P);
    Console.OUT.println("The value of pi is " + pi);
  }
}
