import clocked.Clocked;

public class TestLocal{


  public static def main(Rail[String]) {


    val c = Clock.make();
    val op = int.+;
 
    val a: int @ Clocked[int](c, op);
    async { a = 5;}
    
  
  }
}
