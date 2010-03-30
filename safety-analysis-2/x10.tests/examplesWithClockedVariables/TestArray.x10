import clocked.Clocked;

public class TestArray{



  public static def main(Rail[String]) {
    val c = Clock.make();
    val op = int.+;
    val D = Dist.makeUnique();
    val a:Array[double @ Clocked[int](c, op)](1) = Array.make[double](D, (p:Point)=> 0.0);
    async a(0) = 2.0;
  
  }
}
