import clocked.Clocked;

public class TestArray{



  public static def main(Rail[String]) {
    val c = Clock.make();
    val op = Double.+;
    val D = 10;
    val b =  new Array[double](0..10, (p:Point)=> 0.0);
    val a =  new Array[double @ Clocked[double](c, op, 0.0)](0..D, (Point) => 3.0);
    Console.OUT.println(a(0));
    finish async clocked(c) a(0) = 2.0;
    next;
    Console.OUT.println(a(0));
  
  }
 
}
