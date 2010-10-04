/**
 * Version of Integrate with exp hardwired to 2.
 *
 * Sample program using Guassian Quadrature for numerical integration.
 * Inspired by a 
 * <A href="http://www.cs.uga.edu/~dkl/filaments/dist.html"> Filaments</A>
 * demo program.
 * 
 */
object Integrate2 {
  val errorTolerance:Double = 1.0e-12;
  val reps:Int = 10;

  def main(args:Array[String]) {
    val start:Double = 0.0;
    val end:Double = 1536.0; // 2048.0; // 8192.0;

    println("Integrating from " + start + " to " + end);

    for (i <- 1 until reps) {
      val startTime = System.nanoTime();
      val a = computeArea(start, end);
      val now = System.nanoTime();
      val delta:Double = now - startTime;
      val s = delta/1e9;
      print("Time: "+ s);
      println(" Area: " + a);
    }
  }

  def computeArea(start:Double, end:Double):Double = {
    val l = start;
    val r = end;
    return recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, 0);
  }

  def recEval(l:Double, r:Double, fl:Double, fr:Double, a:Double):Double = {
    val h:Double = (r - l) * 0.5;
    val c:Double = l + h;
    val fc:Double = (c * c + 1.0) * c; 
    val hh:Double = h * 0.5;
    val al:Double = (fl + fc) * hh; 
    val ar:Double = (fr + fc) * hh;
    val alr:Double = al + ar;
    var diff:Double = alr - a;

    if (diff < 0) diff = -diff;
    if (diff <= errorTolerance) {
        return alr;
    } else {
        return recEval(c, r, fc, fr, ar) + recEval(l, c, fl, fc, al);
    }
  }
}

  
