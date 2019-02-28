package futuresched.benchs.xsimpletests;

//import x10.lang.Runtime;

public class AsyncWhenTest {

	public static def main(Rail[String]) {
		
		//finish for (p in Place.places()) {
		//	at (p) async Console.OUT.println("Hello World from place "+p.id);
		//}
		//
		//async { Console.OUT.println("In"); }
		//Console.OUT.println("Out");
		
		
		val b1 = new Cell[Boolean](false);
		val b2 = new Cell[Boolean](false);
		finish {
         Runtime.runAsyncWhen(()=>b1(), ()=>{
            Console.OUT.println("Inside when 1");
            Console.OUT.println("Setting condition for 2");
            b2() = true;
         });
         
         Console.OUT.println("Setting condition for 1");
         atomic b1() = true;
         Runtime.runAsyncWhen(()=>b2(), ()=>{
            Console.OUT.println("Inside when 2");
         });
      }
	}
}


