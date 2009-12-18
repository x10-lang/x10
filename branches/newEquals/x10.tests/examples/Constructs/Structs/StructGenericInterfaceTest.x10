import harness.x10Test;

struct S[T] implements (Int,Int)=>T {
  private val data:Array[T](2);

  public def this(array:Array[T](2)) {
    data = array;
  }

  public def apply(i:Int, j:Int) = data(i, j);
}

public class StructGenericInterfaceTest extends x10Test {
  public def run(): boolean {
    val d = (([1..5,1..5] as Region) -> here) as Dist(2);
    val a = Array.make[Int](d, (p:Point(2)) => p(0)+p(1));
    val s = S[Int](a);

    chk(s(1,1) == 2);
    chk(s(4,4) == 8);

    return true;
  }

  public static def main(Rail[String]) {
    new StructGenericInterfaceTest().execute();
  }

}

