/* Will work only with 2^n elements */


import x10.io.Console;

public class MergeSortOrig{



    public def this () {
    } 	

	static val op = Int.+;

    static val N: int = 128;
    public static def main(args:Rail[String]!){

         Console.OUT.println("Merge Sort ");
        
         val h = new MergeSortOrig();  // final variable 
         val red = Rail.make[int](N, (i:int)=> N-i);
         val black = Rail.make[int](N);
      
     	 var i: int = 0;
     	
     	 for (i = 0; i < N; i++)
     		Console.OUT.print(red(i) + " ");
     	 val level = 0;
     	 h.sort( red, black,  0, N-1, level); 
     	 next;
     	 Console.OUT.println("\nSorted Rail");
     	 for ( i = 0; i < N; i++)
     		Console.OUT.print(red(i) + " ");
         
      
    }
    
 
    
    /** x10doc comment for myMethod */;
    public def sort (red: Rail[int]!, black: Rail[int]!, start: int, end: int, level: int) 
    {
    	
    	val fstart: int = start;
    	val fend: int = start + (end - start)/2;
    	val sstart: int = fend + 1;
    	val ssend: int = end;

        if (start == end) {
                black(start) = red(start);
                return;

        }
     			
        	
         /* Sort into red */
        finish {
        	async sort ( red, black, fstart, fend, level + 1);
  			sort (red, black, sstart, ssend, level + 1);
        }
        
        if (level%2 == 1)
                /* merge from black into red */
                merge(red, black,  fstart, fend, sstart,
                        ssend);
        else /* merge from red into black */
                merge(black, red, fstart, fend, sstart, ssend);

  
    }
    
    public def merge ( a: Rail[int]!, b:Rail[int]!,  fstart: int, fend: int,
    		sstart: int, ssend: int) {

    	
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