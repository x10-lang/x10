import harness.x10Test;

interface Sum {
  def sum():int;
}

struct S implements Sum {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }
  public def typeName() = "S";

  public final def sum() = x + y;
}

class C implements Sum {
  val f1:S;
  
  public def this(a:S) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

public class StructInterfaceTest extends x10Test {
  public def run(): boolean {
    val a = S(3,4);
    val b = new C(a);
    
    chk(a.sum() == 7, "a.sum() == 7");
    chk(b.sum() == 10, "b.sum() == 10");

    return true;
  }

  public static def main(Rail[String]) {
    new StructInterfaceTest().execute();
  }

}

