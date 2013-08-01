package futuresched.benchs.xsimpletests;

//import x10.lang.Runtime;

import futuresched.core.*;
import x10.util.ArrayList;
import x10.util.Box;

public class AsyncWaitTest {

	public static def main(Rail[String]) {
		
		//finish for (p in Place.places()) {
		//	at (p) async Console.OUT.println("Hello World from place "+p.id);
		//}
		//
		//async { Console.OUT.println("In"); }
		//Console.OUT.println("Out");
		
		
		val f1 = new Future[Box[Int]]();
		val f21 = new Future[Box[Int]]();
		val f22 = new Future[Box[Int]]();
		val fs1 = new ArrayList[Future[Box[Int]]]();
		val fs2 = new ArrayList[Future[Box[Int]]]();

		fs1.add(f1);
		fs2.add(f21);
		fs2.add(f22);

		finish {
         FTask.asyncWait(fs1, ()=> {
            Console.OUT.println("Inside wait 1");
            Console.OUT.println("Setting condition for 2");
            f22.set(new Box[Int](2));
         });
         
         FTask.asyncWait(fs2, ()=>{
            Console.OUT.println("Inside wait 2");
         });
         
         async {
           f1.set(new Box[Int](1));
           f21.set(new Box[Int](2));
         }
      }
	}
}



