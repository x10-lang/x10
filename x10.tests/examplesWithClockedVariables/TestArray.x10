import clocked.Clocked;

public class TestArray{



  public static def main(Rail[String]) {
    val c = Clock.make();
    val op = Double.+;
    val D = 10;
    val a =  Array.make[double @ Clocked[double](c, op, 0.0)](0..D, (p:Point)=> 0.0);
    finish async clocked(c) a(0) = 2.0;
    next;
    Console.OUT.println(a(0));
  
  }
 
}
