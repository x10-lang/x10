/* Will work only with 2^n elements */

import clocked.*;
import x10.io.Console;

public class TestNext{



    public def this () {
    } 	

	static val op = Int.+;

    
    public static def main(args:Rail[String]!){
	finish {
    	 val M = 500; // no. of iterations
    	 val N = Int.parseInt(args(0));

         val c: Clock = Clock.make();
         shared var x: int @ Clocked[Int](c,op,0) = 0;
         
      
     	 var i: int = 0;
     	
     	 for (i = 0; i < N; i++)
     		async clocked(c) {
			var j:int = 0;
			for (j = 0; j < M; j++) {
					x = 1;
					next;
			}
		 }
	var j:int = 0;
	for (j = 0; j < M; j++) {
		x = 1;
		next;
	}
    
    }
   } 
    
}
