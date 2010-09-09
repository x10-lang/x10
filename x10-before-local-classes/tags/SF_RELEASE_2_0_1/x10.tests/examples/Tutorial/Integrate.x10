/*
 *
 * (C) Copyright IBM Corporation 2009
 *
 *  This is an example program used in the X10 2.0 Tutorial.
 *
 */

/**
 * This is a slightly more realistic example of the
 * basic computational pattern of using async/finish
 * to express recursive divide-and-conquer algorithms.
 * The program does integration via Guassian Quadrature.
 * <p>
 * It also can serve as an example of using a closure.
 */
public class Integrate { 
  const epsilon = 1.0e-12;

  val fun:(double)=>double;

  static final class resHolder { var value:double; }

  public def this(f:(double)=>double) { fun = f; }

  public def computeArea(left:double, right:double) {
    return recEval(left, fun(left), right, fun(right), 0);
  }

  private def recEval(l:double, fl:double, r:double, fr:double, a:double) {
    val h = (r - l) / 2;
    val hh = h / 2;
    val c = l + h;
    val fc = fun(c);
    val al = (fl + fc) * hh;   
    val ar = (fr + fc) * hh;
    val alr = al + ar;
    if (Math.abs(alr - a) < epsilon) return alr;
    val resHolder = new resHolder();
    var expr2:double = 0;
    finish {
      async { resHolder.value = recEval(c, fc, r, fr, ar); };
      expr2 = recEval(l, fl, c, fc, al);
    }
    return resHolder.value + expr2;
  }
 
  public static def main(args:Rail[String]!) {
    val obj = new Integrate((x:double)=>(x*x + 1.0) * x);
    val xMax = args.length > 0 ? Int.parseInt(args(0)) : 10;
    val area = obj.computeArea(0, xMax);
    Console.OUT.println("The area of (x*x +1) * x from 0 to "+xMax+" is "+area);
  }
}
