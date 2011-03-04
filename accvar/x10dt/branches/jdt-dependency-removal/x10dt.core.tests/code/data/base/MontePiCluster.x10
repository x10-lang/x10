import x10.io.Console;
import x10.util.Random;
import x10.util.Timer;
public class MontePiCluster {
   public static def countAtP(threads:Int, n:Long) {
      val count = new Cell[Long](0);
      finish for (var j: Int = 1; j<= threads; j++)  {
         val jj = j;
         async {
             val r = new Random(Timer.nanoTime());
             val rand = () => r.nextDouble();
            val jCount = countPoints(n, rand);
            atomic count.set(count.value + jCount);
            
         }
      }
      return count.value;
   }
   public static def countPoints(n:Long, rand:()=>Double) {
      var inCircle: Long = 0;
      for (var j: Long=1; j<=n; j++) {
         val x = rand();
         val y = rand();
         if (x*x +y*y <= 1.0) inCircle++;
      }
      return inCircle;
   }
   public static def main(args: Array[String](1)) {
      val N = args.size()>0 ? Long.parse(args(0)) : 1000000L;
      val maxP = args.size()>1 ? Int.parse(args(1)) : 4;
     val tPerP = args.size()>2 ? Int.parse(args(2)) : 2;
     val nPerT = N/(maxP * tPerP); // points per thread
     val inCircle = new Array[Long](1..maxP);
     finish for(var k: Int = 1; k<=maxP; k++) {
          val kk = k;
          val p = Place.place(k-1);
          async inCircle(kk) = at(p) countAtP(kk,nPerT);
     }
     var total: Long = 0;
     for(var k: Int =1; k<=maxP; k++) total += inCircle(k);
     val pi = (4.0*total)/N;
     Console.OUT.println("The value of pi is " + pi);
  }
}