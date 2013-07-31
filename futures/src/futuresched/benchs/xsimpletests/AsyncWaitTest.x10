
//import x10.lang.Runtime;

import java.lang.Future;

public class AsyncWaitTest {

	public static def main(Rail[String]) {
		
		//finish for (p in Place.places()) {
		//	at (p) async Console.OUT.println("Hello World from place "+p.id);
		//}
		//
		//async { Console.OUT.println("In"); }
		//Console.OUT.println("Out");
		
		
		val f1 = new Future[Int]();
		val f2 = new Future[Int]();
		val fs1 = new ArrayList[Future]();
		val fs2 = new ArrayList[Future]();
		fs1.add(f1);
		fs2.add(f2);
		
		finish {
         Runtime.runAsyncWait(fs1, ()=> {
            Console.OUT.println("Inside wait 1");
            Console.OUT.println("Setting condition for 2");
            
         });
         
         
         Runtime.runAsyncWait(fs2, ()=>{
            Console.OUT.println("Inside wait 2");
         });
         
         async {
           
         }
      }
	}
}



