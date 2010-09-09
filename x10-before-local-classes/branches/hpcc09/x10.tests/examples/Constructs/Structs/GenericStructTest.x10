import harness.x10Test;

struct Pair[X,Y] {
  val x:X;
  val y:Y;

  const dummy:int = 100;

  public def this(a:X, b:Y) { x = a; y = b; }

  public def toString() = "<"+x+", "+y+">";

  public def first() = x;
  public def second() = y;

  public static def doit(x:int) = dummy + x;
}

public class GenericStructTest extends x10Test {

  public def run(): boolean {
    val s1 = Pair[Int, String](100, "hello");

    chk(s1.toString().equals("<100, hello>"), "toString failure");
    chk(s1.first() == 100);
    chk(Pair.doit(1000) == 1100);

    return true;
  }

  public static def main(Rail[String]) {
    new GenericStructTest().execute();
  }
}
