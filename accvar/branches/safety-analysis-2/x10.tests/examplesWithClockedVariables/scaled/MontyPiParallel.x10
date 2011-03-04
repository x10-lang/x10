import x10.util.Random;
import x10.io.Console;
import clocked.Clocked;


public class MontyPiParallel {

  public static  def main(s: Rail[String]) {
    val start_time = System.currentTimeMillis(); 

    val P = 64;
    val N = 10000000; 
    val c = Clock.make();
    val op = Double.+;
    shared var result: double @ Clocked[Double](c, op, 0.0) = 0.0;
    finish  {
   	 var i : Int = 0;
        for(i = 0; i < P; i++) {

        async clocked(c) {
          
          val r = new Random();
          var a:double=0.0D;
          for(j in 1..N) {
                  val x = r.nextDouble(), y=r.nextDouble();
                  if (x*x +y*y <= 1.0) a++;
          }
          result = a;
        }
      }
    }  
    next;
    val pi = 4*result/(N*P);
    Console.OUT.println("The value of pi is " + pi);
    val compute_time = (System.currentTimeMillis() - start_time);
    Console.OUT.println( compute_time + " ");
  }
}
