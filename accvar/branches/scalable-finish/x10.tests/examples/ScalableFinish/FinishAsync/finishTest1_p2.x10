/**
 * Description: This program tests simple finish: a finish only contains a single 
 * async or at or method call.
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
public class finishTest1_p2 {
     public def f1():void {
    	 // method contains async
    	 async(here.next()){}
     }
     public def f2():void {
    	 // method contains at
    	 at(here){}
     } 
     public def run() {
	
    	 //TODO: test code
    	 var i:int = 0;
    	 // finish with single async
    	 finish{
    		 async{}
    	 }
    	 // finish with single at
    	 finish{
    		 at(here.next()){}
    	 }
    	 
    	 // finish with other statements
    	 finish{
    		 i = i + 1;
    	 }
    	 
    	 // finish with method call
    	 finish{
    		 f1();
    	 }
    	 finish{
    		 f2();
    	 }
    	 
     }
     
     public static def main(args: Rail[String]) {
    	 new finishTest1_p2().run();
     }
 }



