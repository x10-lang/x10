import clocked.Clocked;

public class TestFormalArray{

  public static def testFormal(c: Clock, op: (int,int)=>int, myA:Array[int @ Clocked[int](c,op)](1)) {
 		finish  ateach ((p) in myA.dist) { myA(p) = 1;}
   	
   
  }
  public static def main(Rail[String]) {


    val c = Clock.make();
    val op = int.+;
    val D = Dist.makeUnique();
    val a:Array[int @ Clocked[int](c, op)](1) = Array.make[int](D, (p:Point)=> p(0));
    testFormal(c, op, a);
  
  }
}
