import harness.x10Test;

struct S {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }
  public def typeName() = "S";

  public final def sum() = x + y;

  public final def sum4(o:S) = sum() + o.sum() + o.x;
}

class C {
  val f1: S;
  
  public def this(a:S) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

public class StructTest1 extends x10Test {
  public def run(): boolean {
    val a: S = S(3,4);
    val b: S = S(10,20);
    
    chk(a.sum() == 7, "a.sum() == 7");
    chk(a.sum4(b) == 47, "a.sum4(b) == 47");

    chk(new C(S(100, 50)).sum() == 153);

    chk(test1(a, b) == 40);
    
    return true;
  }

  public static def test1(a:S, b:S) {
    return a.sum() + b.sum() + a.x;
  }

  public static def main(Rail[String]) {
    new StructTest1().execute();
  }

}

