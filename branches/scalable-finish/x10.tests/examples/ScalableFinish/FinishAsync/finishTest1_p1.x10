/**
 * Description: This program tests simple finish: a finish only contains a single 
 * async or at or method call.
 * Expected Result: run() returns true if successful, false otherwise.
 * @author Baolin Shao (bshao@us.ibm.com)
 */
import x10.compiler.*;
public class finishTest1_p1 {
     public def f1():void {
    	 // method contains async
    	 async{}
     }
     public def f2():void {
    	 // method contains at
    	 at(here){}
     } 
     public def run() {
	
    	 //TODO: test code
    	 var i:int = 0;
    	 // finish without any statement
    	 @FinishAsync(23,16,true,1)
         finish{
    		 
    	 }
    	 // finish with single async
    	 @FinishAsync(23,16,true,1)
         finish{
    		 async{}
    	 }
    	 // finish with single at
    	 
    	 
    	 @FinishAsync(23,16,true,1)
         finish{
    		 at(here){}
    	 }
    	 
    	 @FinishAsync(23,16,true,1)
    	 finish{
    		 i = i + 1;
    	 }
    	 
    	 // finish with method call
    	 
    	 @FinishAsync(23,16,true,1)
         finish{
    		 f1();
    	 }
    	 
    	 @FinishAsync(23,16,true,1)
         finish{
    		 f2();
    	 }
    	 
     }
     
     public static def main(args: Rail[String]) {
    	 new finishTest1_p1().run();
     }


 }



