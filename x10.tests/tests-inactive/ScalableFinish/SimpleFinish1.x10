import x10.compiler.FinishAsync;
import x10.util.Timer;
public class SimpleFinish1 {
    public static def main(args: Rail[String]) //throws Exception
    {
    val start = Timer.nanoTime();
    finish{
            var i:int = 0;
	    for(i=0;i<1000;i++){
		val p1 = Place.place(i % Place.numPlaces());
	    	async at (p1){          	
            		val p = here;
            		@FinishAsync(1,1,false,2)
            		finish{
                    		async at (p.next().next()){ }
                    		async at (p.next()){
					async{}
                    		}
                   		async at (p.next()){ 
                        		async{
                                		async{}
                            		}
                    		}
			} 
		}
	    }
    }
    val end = Timer.nanoTime();
    Console.OUT.println("time = "+(end-start)+" nanoseconds");

     }
}
