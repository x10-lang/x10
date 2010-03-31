/* Will work only with 2^n elements */

import clocked.*;
import x10.io.Console;

public class MergeSort{



    public def this () {
    } 	

	static val op = Int.+;

    static val N: int = 128;
    public static def main(args:Rail[String]!){

         Console.OUT.println("Merge Sort ");
         val c: Clock = Clock.make();
         val h = new MergeSort();  // final variable 
         val red : Rail[Int @ Clocked[Int] (c, op)]! = Rail.make[int](N, (i:int)=> N-i);
         val black: Rail [Int @ Clocked[Int](c, op)]! = Rail.make[int](N, (i:int)=> 0);
         val level = 0;
     	 var i: int = 0;
     	
     	 for (i = 0; i < N; i++)
     		Console.OUT.print(red(i) + " ");
     	 h.sort(c, red, black, 0, N-1, level); 
     	 Console.OUT.println("\nSorted Rail");
     		for ( i = 0; i < N; i++)
     		Console.OUT.print(red(i) + " ");
         
      
    }
    
 
    
    /** x10doc comment for myMethod */;
    public def sort (c: Clock, red: Rail[int @ Clocked[int] (c, op)]!, black: Rail[int @ Clocked[int] (c, op)]!,start: int, end: int, level: int) 
    @ ClockedM (c) {
    	
    	val fstart: int = start;
    	val fend: int = start + (end - start)/2;
    	val sstart: int = fend + 1;
    	val ssend: int = end;

        if (start == end)
        {
        	black(start) = red(start);
        	return;
        	   
        }
        	
         /* Sort into black */
        async clocked (c) sort (c, red, black, fstart, fend, level+1);
        /* Sort into red */
		sort (c, red, black, sstart, ssend, level+1);
        next; /* Like a finish */
        
        if (level%2 == 1)
        	/* merge from black into red */
        	merge(c, red, black,  fstart, fend, sstart,
    			ssend);
        else /* merge from red into black */
        	merge(c, black, red, fstart, fend, sstart, ssend);
   	
    }
    
    public def merge (c: Clock, a: Rail[int @ Clocked[int] (c, op)]!, b: Rail[int @ Clocked[int] (c, op)]!, fstart: int, fend: int,
    		sstart: int, ssend: int) @ ClockedM (c) {
   // public def merge (c: Clock, a: Rail[int]!, b: Rail[int]!, fstart: int, fend: int,
   // 		sstart: int, ssend: int) @ ClockedM (c) {
    	
    var x: int = fstart;
    var y: int = sstart;
    var z: int = fstart;
    val size = (ssend - fstart) + 1;
  
    
    while(x <= fend && y <= ssend)
    {
           if(a(x) < a(y))
            {
                   b(z++) = a(x++);
                                   
                    
            }
            else
            {
            
            	   b(z++) = a(y++);
            }
    }
  //copy remaining elements to the tail of b[];
    while(x <= fend)
    {
            b(z++) = a(x++);
    }
    while(y <= ssend)
    {
            b(z++) = a(y++);
    }
	
      	
    }
    
}