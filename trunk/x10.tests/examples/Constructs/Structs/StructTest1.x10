import harness.x10Test;

struct S1 {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }

  public final def sum() = x + y;

  public final def sum4(o:struct S1) = sum() + o.sum() + o.x;
}

struct S2 extends struct S1 {
  val z:int;
  public def this(a:int, b:int, c:int) {
    super(a, b);
    z =c;
  }
  
  public final def sum2() = sum() + z;
}

class C {
  val f1:struct S2;
  
  public def this(a:struct S2) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

public class StructTest1 extends x10Test {
  public def run(): boolean {
    val a:struct S1 = S1(3,4);
    val b:struct S2 = S2(1, 2,3);
    
    chk(a.sum() == 7, "a.sum() == 7");
    chk(b.sum() == 3, "b.sum() == 3");
    chk(b.sum2() == 6, "b.sum2() == 6");
    chk(b.sum4(a) == 13, "b.sum4(a) == 13");

    chk(new C(S2(100, 50, 1000)).sum() == 153);

    chk(test1(a, b) == 16);
    
    return true;
  }

  public static def test1(a:struct S1, b:struct S2) {
    return a.sum() + b.sum2() + a.x;
  }

  public static def main(Rail[String]) {
    new StructTest1().execute();
  }

}

