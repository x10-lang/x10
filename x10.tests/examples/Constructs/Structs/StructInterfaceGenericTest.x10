import harness.x10Test;

interface Sum {
  def sum():int;
}

struct S implements Sum {
  val x:int;
  val y:int;

  public def this(a:int, b:int) { x = a; y = b; }

  public final def sum() = x + y;
}

class C implements Sum {
  val f1:S;
  
  public def this(a:S) { f1 = a; }

  public def sum() = f1.sum() + 3;
}

class Summer[T]{T<:Sum} {
   // WORKAROUND XTENLANG-898 by using a cast instead of constraining a to be T!.
   // As soon as 898 is fixed, revert to the commented out version of the method.
   // def sum(a:T!) = a.sum();
   def sum(a:T) = (a as T!).sum();
}

public class StructInterfaceGenericTest extends x10Test {
  public def run(): boolean {
    val a = S(3,4);
    val b = new C(a);
    val sa = new Summer[S]();
    val sb = new Summer[C]();

    chk(sa.sum(a) == 7, "sa.sum(a) == 7");
    chk(sb.sum(b) == 10, "sb.sum(b) == 10");

    return true;
  }

  public static def main(Rail[String]) {
    new StructInterfaceGenericTest().execute();
  }

}

