import harness.x10Test;

interface Sum {
  def sum():int;
}

struct S1 implements Sum {
  val x:int;
  val y:int;
  const FF:int = S1(100,200).sum();

  public def this(a:int, b:int) { x = a; y = b; }

  public final def sum() = x + y;
}

class C implements Sum {
  val f1:S1;
  
  public def this(a:S1) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

public class StructInterface extends x10Test {
  public def run(): boolean {
    val a:S1 = S1(3,4);
    chk(a.sum() == 7, "a.sum() == 7");
    return true;
  }

  public static def main(Rail[String]) {
    new StructInterface().execute();
  }
}
