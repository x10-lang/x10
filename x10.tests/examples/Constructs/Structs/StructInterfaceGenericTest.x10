import harness.x10Test;

interface StructInterfaceGenericTest_Sum {
  def sum():int;
}

struct StructInterfaceGenericTest_S implements StructInterfaceGenericTest_Sum {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }

  public final def sum() = x + y;
}

class StructInterfaceGenericTest_C implements StructInterfaceGenericTest_Sum {
  val f1:StructInterfaceGenericTest_S;
  
  public def this(a:StructInterfaceGenericTest_S) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

class Summer[T]{T<:StructInterfaceGenericTest_Sum} {
   // WORKAROUND XTENLANG-898 by using a cast instead of constraining a to be T!.
   // As soon as 898 is fixed, revert to the commented out version of the method.
   // def sum(a:T!) = a.sum();
   def sum(a:T) = (a as T!).sum();
}

public class StructInterfaceGenericTest extends x10Test {
  public def run(): boolean {
    val a = StructInterfaceGenericTest_S(3,4);
    val b = new StructInterfaceGenericTest_C(a);
    val sa = new Summer[StructInterfaceGenericTest_S]();
    val sb = new Summer[StructInterfaceGenericTest_C]();

    chk(sa.sum(a) == 7, "sa.sum(a) == 7");
    chk(sb.sum(b) == 10, "sb.sum(b) == 10");

    return true;
  }

  public static def main(Rail[String]) {
    new StructInterfaceGenericTest().execute();
  }

}

