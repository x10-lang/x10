import clocked.Clocked;

public class TestFormalRail{

  public static def testFormal(c: Clock, op: (int,int)=>int, myA:Rail[int @ Clocked[int](c,op)]!) {
 		myA(0) = 1;
   	
   
  }
  public static def main(Rail[String]) {


    val c = Clock.make();
    val op = int.+;
 
    val a:Rail[int @ Clocked[int](c, op)]! = Rail.make[int](5, (Int)=> 0);
    testFormal(c, op, a);
  
  }
}
