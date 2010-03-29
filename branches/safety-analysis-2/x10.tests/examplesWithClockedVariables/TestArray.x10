import clocked.Clocked;

public class TestArray{



  public static def main(Rail[String]) {
    val c = Clock.make();
    val op = int.+;
    val D = Dist.makeUnique();
    val a:Array[int @ Clocked[int](c, op)](1) = Array.make[int](D, (p:Point)=> p(0));
    async a(0) = 2;
  
  }
}
