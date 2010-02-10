import harness.x10Test;

interface StructInterface_Sum {
  def sum():int;
}

struct StructInterface_S1 implements StructInterface_Sum {
  val x:int;
  val y:int;
  const FF:int = StructInterface_S1(100,200).sum();

  public def this(a:int, b:int) { x = a; y = b; }

  public final def sum() = x + y;
}

class StructInterface_C implements StructInterface_Sum {
  val f1:StructInterface_S1;
  
  public def this(a:StructInterface_S1) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

public class StructInterface extends x10Test {
  public def run(): boolean {
    val a:StructInterface_S1 = StructInterface_S1(3,4);
    chk(a.sum() == 7, "a.sum() == 7");
    return true;
  }

  public static def main(Rail[String]) {
    new StructInterface().execute();
  }
}
