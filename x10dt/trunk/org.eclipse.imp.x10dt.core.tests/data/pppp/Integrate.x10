public class Integrate { 
  const epsilon = 1.0e-12;
  val fun:(double)=>double;
  public def this(f:(double)=>double) { fun = f; }
  public def computeArea(left:double, right:double) =
    eval(left, fun(left), right, fun(right), 0);

    private def eval(l:double, fl:double, r:double, fr:double, a:double):double
    {
        val h = (r - l) / 2;
        val hh = h / 2;
        val c = l + h;
        val fc = fun(c);
        val al = (fl + fc) * hh;   
        val ar = (fr + fc) * hh;
        val alr = al + ar;
        return (Math.abs(alr - a) < epsilon) ?
            alr : 
            eval(c, fc, r, fr, ar)
            + eval(l, fl, c, fc, al);
    }
  public static def main(args:Rail[String]) 
    {
        val obj = new Integrate((x:double)=>(x*x + 1.0) * x);
        val xMax = args.length > 0 ? Int.parseInt(args(0)) : 10;
        var start:double = - System.nanoTime();
        val area = obj.computeArea(0, xMax);
        start += System.nanoTime();
        Console.OUT.println("The area of (x*x +1) * x from 0 to "
                            +xMax+" is "+area 
                            + " (t=" + (start/(1000*1000) as float) + " ms)");
    }
}
